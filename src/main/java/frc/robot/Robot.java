package frc.robot;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType; 
import com.revrobotics.spark.config.SparkMaxConfig;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;

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
public class Robot extends TimedRobot {
  
  //definicao dos motores
  SparkMax frontLeft = new SparkMax(2, MotorType.kBrushed);
  SparkMax frontRight = new SparkMax(4, MotorType.kBrushed);
  SparkMax backLeft = new SparkMax(3, MotorType.kBrushed);
  SparkMax backRight = new SparkMax(5, MotorType.kBrushed);
  SparkMax escaladaCorda = new SparkMax(6, MotorType.kBrushless);
  WPI_TalonSRX braco = new WPI_TalonSRX(7); 
  WPI_TalonSRX giroBraco = new WPI_TalonSRX(8); 

  DifferentialDrive drive;

  //definicao do joystick
  Joystick xBoxController1 = new Joystick(0); //
  Joystick xBoxController2 = new Joystick(1); //

  //Criar variavel para guardar o tempo
  double startTime;

  public Robot() {
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

    if (elapsed < 3) { //se o tempo for menor que 3 segundos, anda pra frente
        frontLeft.set(-0.25); //motores
        backLeft.set(-0.25);
        frontRight.set(0.25); 
        backRight.set(0.25);

        giroBraco.set(ControlMode.PercentOutput, 0.0);
    } 

    else if (elapsed < 4) { //tempo entre 3 e 4 segundos, movimento para e braço vai levemente para trás para contrariar braco nao totalmente fixo
      frontLeft.set(0);
      backLeft.set(0);
      frontRight.set(0);
      backRight.set(0);

      braco.set(ControlMode.PercentOutput, -0.1);
      
      }
    else if (elapsed < 5) { //tempo entre 4 e 5 segundos, movimento para e braco gira
        frontLeft.set(0);
        backLeft.set(0);
        frontRight.set(0);
        backRight.set(0);
 
        giroBraco.set(ControlMode.PercentOutput, 0.3); 
    } 
    else { //tempo maior que 4 segundos, para tudo
        frontLeft.set(0);
        backLeft.set(0);
        frontRight.set(0);
        backRight.set(0);

        giroBraco.set(ControlMode.PercentOutput, 0.0);
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

boolean girobola = xBoxController1.getRawButton(6); // botao 6 = RB

//definicao dos botoes do braço
boolean armUp = xBoxController1.getRawButton(4); //botao 4 = Y
boolean armDown = xBoxController1.getRawButton(1); // botao 1 = A

//definicao dos botoes do giro do braço
boolean directionFront = xBoxController1.getRawButton(2); //botao 2 = B
boolean directionBack = xBoxController1.getRawButton(3); // botao 3 = X

//se um botao estiver pressionado, o motor de escalada sobe ou desce
//if (climbUp) {
//   escaladaCorda.set(1); //100% da potencia
// } else if (climbDown) {
//  escaladaCorda.set(-1); //100% da potencia
  
// } else {
//  escaladaCorda.set(0);
 //}

//se um botao estiver pressionado, o braço anda para cima ou para baixo
if (armUp) {
   braco.set(ControlMode.PercentOutput, 0.2);
 } else if (armDown) {
   braco.set(ControlMode.PercentOutput, -0.2);
  
 } else { 
   braco.set(ControlMode.PercentOutput, 0);
 }

//se um botao estiver pressionado, o giro do braço anda para frente ou para tras
if (girobola) {
  giroBraco.set(ControlMode.PercentOutput, 0.7); // velocidade maior
} else if (directionFront) {
  giroBraco.set(ControlMode.PercentOutput, 0.35); // frente normal
} else if (directionBack) {
  giroBraco.set(ControlMode.PercentOutput, -0.3); // trás normal
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
