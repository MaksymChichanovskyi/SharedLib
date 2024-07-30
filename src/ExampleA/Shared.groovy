package ExampleA


def CheckoutGit(){
        checkout changelog: false, scm: [$class: 'GitSCM', branches: [[name: '*/main']],
                                             extensions       : [[$class: 'LocalBranch', localBranch: '**'], [$class: 'UserIdentity', name: 'Jenkins']],
                                             userRemoteConfigs: [[credentialsId: 'cf84bbaf-792c-4bac-98ae-b80958b2656f', refspec: '+refs/heads/main:refs/remotes/origin/main',
                                             url: 'https://github.com/MaksymChichanovskyi/MavenProject.git']]]
}


def startBuild (String imageName = "maven:3.9.8-amazoncorretto-11") {
        docker.image(imageName).pull()
        docker.image(imageName).inside() {
            sh "mvn clean package"
    }

def updatePom(String pomFilePath) {
    def buildNumber = env.BUILD_NUMBER ?: '1.0-SNAPSHOT'
    
    def pomFile = new File(pomFilePath)
    def pomXml = pomFile.text
    
    def xml = new XmlSlurper().parseText(pomXml)
    
    // Find and update the <version> tag
    xml.version[0].value = buildNumber
    
    def updatedXml = XmlUtil.serialize(xml)
    pomFile.text = updatedXml
    
    echo "Updated <version> to ${buildNumber} in ${pomFilePath}"
   }
}


return this 
