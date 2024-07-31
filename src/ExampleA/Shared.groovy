package ExampleA

import groovy.xml.XmlSlurper
import groovy.xml.XmlUtil

    def defaultCheckout() {
        checkout(scm)
    }

    def startBuild(String imageName = "maven:3.9.8-amazoncorretto-11") {
        docker.image(imageName).pull()
        docker.image(imageName).inside() {
            sh "mvn clean package"
        }
    }



  def updatePomVersion(String filePath, String newVersion) {
   def project = new XmlSlurper().parse(new File("/tmp/jenkins/workspace/MavenProject/target/pom.xml"))
   def version = project.version.toString()
   def mainversion = version.substring(0, version.indexOf("-SNAPSHOT"))
   def pomFile = 'pom.xml'
   def pom = readMavenPom(file: pomFile)
    pom.version = versionTag // де versionTag - це бажана версія
writeMavenPom(file: pomFile, model: pom)
 }
    /* def getJarSize() {
    def jarFile = jarFiles[0]
    def jarFilePath ='/tmp/jenkins/workspace/MavenProject/target/Education.ExampleA-1.0-SNAPSHOT.jar'
    def process = ['stat', '-c', '%s', jarFilePath].execute()
    def fileSizeBytes = process.text.trim().toLong()
    def fileSizeMB = fileSizeBytes / (1024 * 1024)
    echo "JAR File: ${jarFilePath}, Size: ${fileSizeMB} MB"
}*/
    

    def mavenApp(){
        def agentName = 'linux && docker'
        def someText = 'Hello!'
         

node(agentName) { 
    stage('Checkout') {
        defaultCheckout()
    }
    stage('Update Version'){
      updatePomVersion(pomFilePath, newVersion)
     }
    
  stage('Build'){
      startBuild()
    }
    stage ('GetSize'){
    getJarSize()
}

    }

return this
