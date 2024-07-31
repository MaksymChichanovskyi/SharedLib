package ExampleA

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

    /*def updatePomVersion(String $buildNumber) {
          def pomFilePath = '/tmp/jenkins/workspace/MavenProject/pom.xml'
          def pomXml = readFile(pomFilePath)
          def parsedXml = new XmlSlurper(false,false).parseText('pom.xml')
          parsedXml.version.replaceBody(newVersion)
          def updatedXml = XmlUtil.serialize(parsedXml)
           writeFile(file: pomFilePath, text: updatedXml)
    }*/
    def getJarSize() {
    def jarFile = jarFiles[0]
    def jarFilePath ='/tmp/jenkins/workspace/MavenProject/target/'
    def process = ['stat', '-c', '%s', jarFilePath].execute()
    def fileSizeBytes = process.text.trim().toLong()
    def fileSizeMB = fileSizeBytes / (1024 * 1024)
    echo "JAR File: ${jarFilePath}, Size: ${fileSizeMB} MB"
}
    

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
}

    }

return this
