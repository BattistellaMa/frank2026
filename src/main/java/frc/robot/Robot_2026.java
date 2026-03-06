package frc.robot;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
//import edu.wpi.first.wpilibj.XboxController;
//import edu.wpi.first.wpilibj.PS4Controller.Axis;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

/**
 * The methods in this class are called automatically corresponding to each mode, as described in
 * the TimedRobot documentation. If you change the name of this class or the package after creating
 * this project, you must also update the Main.java file in the project.
 */

 //Funcao executada quando o robo liga
public class Robot_2026 extends TimedRobot {
  
  //definicao dos motores
  SparkMax frontLeft = new SparkMax(2, MotorType.kBrushed);
  SparkMax frontRight = new SparkMax(4, MotorType.kBrushed);
  SparkMax backLeft = new SparkMax(3, MotorType.kBrushed);
  SparkMax backRight = new SparkMax(5, MotorType.kBrushed);
  SparkMax escaladaCorda = new SparkMax(6, MotorType.kBrushed);
  WPI_TalonSRX braco = new WPI_TalonSRX(7); 
  WPI_TalonSRX giroBraco = new WPI_TalonSRX(8); //brushless
  WPI_TalonSRX braco2 = new WPI_TalonSRX(9);
  WPI_TalonSRX lancador = new WPI_TalonSRX(10); 

  DifferentialDrive drive;

  //definicao do joystick
  Joystick xBoxController1 = new Joystick(0); //
  Joystick xBoxController2 = new Joystick(1); //

  //Criar variavel para guardar o tempo
  double startTime;

  public Robot_2026() {
    SparkMaxConfig escaladaConfig = new SparkMaxConfig();
    escaladaConfig.inverted(false);
    escaladaCorda.configure(escaladaConfig, ResetMode.kNoResetSafeParameters, PersistMode.kPersistParameters);

    
    // modo neutral do braço
    braco.setNeutralMode(NeutralMode.Brake);
  }
  

  @Override
  public void robotPeriodic() {}



  //quando o robo entra no modo autonomo
  @Override 
  public void autonomousInit() {
    //atribui a variavel startTime o tempo que o robo entrou no modo autonomo
    startTime = Timer.getFPGATimestamp();
  }

  @Override
  public void autonomousPeriodic() {

    //variavel para medir o tempo desde que o robo ligou
    double time = Timer.getFPGATimestamp();
    double elapsed = time - startTime;

    if (elapsed < 1) { //se o tempo for menor que 1 segundo, anda pra frente
        frontLeft.set(-0.25); //motores
        backLeft.set(-0.25);
        frontRight.set(0.25); 
        backRight.set(0.25);

        giroBraco.set(ControlMode.PercentOutput, 0.0);
    } 

    else if (elapsed < 6) { //tempo entre 1 e 6 segundos, lanca as bola
      backLeft.set(0);
      frontRight.set(0);
      backRight.set(0);

      lancador.set(ControlMode.PercentOutput, 0.6);
      
      }
    else if (elapsed < 10) { //tempo entre 6 e 10 segundos, vai para tras erguendo o climber
        frontLeft.set(0.25);
        backLeft.set(0.25);
        frontRight.set(-0.25);
        backRight.set(-0.25);
 
        lancador.set(ControlMode.PercentOutput, 0);
        escaladaCorda.set(0.5);
 
    } 
    
    else if (elapsed < 12) { //tempo entre 10 e 12 segundos, para tudo
      frontLeft.set(0);
      backLeft.set(0);
      frontRight.set(0);
      backRight.set(0);

      escaladaCorda.set( 0); 
    }
    else if (elapsed < 19) {//tempo entre 12 e 19
      frontLeft.set(0);
      backLeft.set(0);
      frontRight.set(0);
      backRight.set(0);

      escaladaCorda.set(-1); 
    }
    else if (elapsed < 20) {
      frontLeft.set(0);
      backLeft.set(0);
      frontRight.set(0);
      backRight.set(0);

      escaladaCorda.set(0); // para o motor de escalada
    }


  }

  @Override
  public void teleopInit() {}


  //quando o robo entra no modo teleoperado
  @Override
  public void teleopPeriodic() {
double speed = xBoxController1.getRawAxis(1); //eixo 1 = L3 pra frente e pra tras
double turn = xBoxController1.getRawAxis(4); //eixo 4 = R3 pros lado

//definicao do botao do motor de escalada
//boolean climbUp = xBoxController2.getRawButton(2); //botao 2 = botao B
//boolean climbDown = xBoxController2.getRawButton(3); // botao 3 = botao X
boolean climbUp = xBoxController1.getRawButton(6); // botao 6 = RB
boolean climbDown = xBoxController1.getRawButton(5); // botao 5 = LB


//boolean girobola = xBoxController2.getRawButton(6); // botao 6 = RB

//definicao dos botoes do braço
int direcao = 0;

boolean armUpDown = xBoxController2.getRawButton(5); //botao 5 = LB
//boolean armDown = xBoxController2.getRawButton(1); // botao 1 = A
if (armUpDown && direcao == 0) { direcao = 1; } // se o botao for pressionado e o braço estiver abaixado, sobe o braço
else if (armUpDown && direcao == 1) { direcao = 0; } // se o botao for pressionado e o braço estiver alto, abaixa o braço

//definicao dos botoes do giro do braço
boolean directionFront = xBoxController2.getRawButton(1); //botao 1 = A
boolean directionBack = xBoxController2.getRawButton(4); // botao 4 = Y

//definicao dos botoes do giro do braço
boolean lancar = xBoxController2.getRawButton(2); //botao 2 = B
boolean lancarForte = xBoxController2.getRawButton(6); // botao  6 = RB 

//se o botao for pressionado, lanca ou solta as bolinhas
if (lancar){
  lancador.set(0.6);
}
if (lancarForte) {
  lancador.set(1);
}
//se um botao estiver pressionado, o motor de escalada sobe ou desce
if (climbDown) {
   escaladaCorda.set(0.5); //50% da potencia
 } else if (climbUp) {
  escaladaCorda.set(-1); //100% da potencia
  
 } //else {
//  escaladaCorda.set(0);
 //}


//se um botao estiver pressionado, o braço anda para cima ou para baixo
if (direcao == 1) {
  braco.set(ControlMode.PercentOutput, 0.2);
  braco2.set(ControlMode.PercentOutput, -0.2);
  Timer.delay(2); // Wait for 2 seconds
  braco.set(ControlMode.PercentOutput, 0.0);
  braco2.set(ControlMode.PercentOutput, 0.0); // Stop the motor after timeout
  // Stop the motor after timeout
 } else if (direcao == 0) {
   braco.set(ControlMode.PercentOutput, -0.2);
   braco2.set(ControlMode.PercentOutput, 0.2); 
   Timer.delay(2);
   braco.set(ControlMode.PercentOutput, 0.0); // Stop the motor after timeout
   braco2.set(ControlMode.PercentOutput, 0.0); // Stop the motor after timeout

 } 

//se um botao estiver pressionado, o giro do braço anda para frente ou para tras
if (directionFront) {
  giroBraco.set(ControlMode.PercentOutput, 0.7); // velocidade maior
} else if (directionFront) {
  giroBraco.set(ControlMode.PercentOutput, 0); // parado
}
if (directionBack) {
  giroBraco.set(ControlMode.PercentOutput, -0.7); // trás normal
} else {
  giroBraco.set(ControlMode.PercentOutput, 0); // parado
}
 
 
 double speedLimit = 0.4; // 50% da velocidade máxima
 double turnLimit = 0.275;  // também pode limitar a curva
 
 speed *= speedLimit;
 turn *= turnLimit;

double left = speed - turn; //
double right = speed + turn; //

    frontLeft.set(left);
    backLeft.set(left);
    frontRight.set(-right);
    backRight.set(-right);

  }

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  @Override
  public void testInit() {}

  @Override
  public void testPeriodic() {}

  @Override 
  public void simulationInit() {}

  @Override
  public void simulationPeriodic() {}
}
