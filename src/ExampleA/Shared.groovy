package ExampleA
import groovy.xml.XmlUtil


def defaultCheckout() {
        checkout(scm)
    }
def readPomXml(){
    def pomFileContent = readFile 'pom.xml'
    return new XmlParser().parseText(pomFileContent)
}

def savePomXml(def pomXml){
    def updatedPomFileContent = groovy.xml.XmlUtil.serialize(pomXml)
    writeFile file: 'pom.xml', text: updatedPomFileContent   
}


def updatePomVersion(String buildNumber, def pomXml)
{
    pomXml.version[0].value = "1.0.${buildNumber}-SNAPSHOT"
    echo "Updated pom.xml with build number: ${buildNumber}"
}
    

 def startBuild(String imageName = "maven:3.9.8-amazoncorretto-11")
    {
        docker.image(imageName).pull()
        docker.image(imageName).inside() {
        sh "mvn clean package"
    }
 }
/*
def getJarPathFromPom(def pomXml){
       
}

def getJarSizeFromPom(def pomXml){
             
    }*/



def getJarSize()
 {
    def jarFilePath = sh(script: 'find target -name "*.jar"', returnStdout: true).trim()
    if (jarFilePath)
    {
      def jarSizeBytes = sh(script: "stat -c%s ${jarFilePath}", returnStdout: true).trim().toLong()
      def jarSizeKB = jarSizeBytes / 1024.0
      return jarSizeKB
    }
    else
    {
        echo "JAR file not found"
        return 0
    }
}


def mavenApp()
{
  def agentName = 'linux && docker'



 node(agentName) {
        stage('Checkout') {
            defaultCheckout()
        }
         def pomXml = readPomXml()
        stage('Update Pom.xml'){
            updatePomVersion(env.BUILD_NUMBER, pomXml)
            savePomXml(pomXml)
        }

        stage('Build'){
            startBuild()
          }
        stage('Get Size'){
           def jarSizeKB = getJarSize()
            echo "JAR file size: ${jarSizeKB} KB"
        }
    }
}
return this
