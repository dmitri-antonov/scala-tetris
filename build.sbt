minSdkVersion in Android := "15"

targetSdkVersion in Android := "21"

platformTarget in Android := "android-21"

buildToolsVersion in Android := Some("21.1.1")

scalaVersion := "2.11.7"

javacOptions ++= Seq(
  "-source", "1.7",
  "-target", "1.7",
  "-Xlint:unchecked",
  "-Xlint:deprecation"
)

scalacOptions ++= Seq(
  "-target:jvm-1.7",
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked",
  "-deprecation",
  "-Xcheckinit"
)

libraryDependencies ++= Seq(
  "com.android.support" % "appcompat-v7" % "21.0.2",
  "org.robolectric" % "robolectric" % "3.0" % "test",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "junit" % "junit" % "4.11" % "test",
  "com.novocode" % "junit-interface" % "0.11" % "test",
  "org.apache.maven" % "maven-ant-tasks" % "2.1.3" % "test"
)

unmanagedClasspath in Test ++= (bootClasspath in Android).value

useProguard in Android := true

proguardScala in Android := true

proguardOptions in Android += "-keepattributes Signature"
proguardOptions in Android ++= scala.io.Source.fromFile(baseDirectory.value.getAbsolutePath+"/proguard.cfg").
  getLines().toSeq.filter(a=>{! a.trim.isEmpty && ! a.contains("#")}).map(a=>{println(a); a})