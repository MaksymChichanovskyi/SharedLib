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
    def pomFilePath = "/tmp/jenkins/workspace/MavenProject/target/pom.xml"
    def newVersion = env.BUILD_NUMBER
    def pomFile = new File(filePath)
    def xml = new XmlSlurper().parse(pomFile)
    xml.version = newVersion
    pomFile.text = xml.toString()
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
      updatePomVersion()
     }
    
  stage('Build'){
      startBuild()
    }
    stage ('GetSize')
    getJarSize()
}

    }

return this
