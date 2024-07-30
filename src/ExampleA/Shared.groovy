package ExampleA

import groovy.xml.XmlSlurper
import groovy.xml.XmlUtil



def CheckoutGit(){
        checkout changelog: false, scm: [$class: 'GitSCM', branches: [[name: '*/main']],
                                             extensions       : [[$class: 'LocalBranch', localBranch: '**'], [$class: 'UserIdentity', name: 'Jenkins']],
                                             userRemoteConfigs: [[credentialsId: 'cf84bbaf-792c-4bac-98ae-b80958b2656f', refspec: '+refs/heads/main:refs/remotes/origin/main',
                                             url: 'https://github.com/MaksymChichanovskyi/MavenProject.git']]]
}

def patchPom(){
        updatePomVersion(String buildNumber) {
        def pomFile = new File("pom.xml")
        
        if (!pomFile.exists()) {
            throw new RuntimeException("File pom.xml does not exist.")
        }

        def pom = new XmlSlurper().parse(pomFile)
        
        // Перевіряємо, чи є версія в pom.xml
        def versionNode = pom.version[0]
        if (versionNode) {
            versionNode.replaceBody(buildNumber)
        } else {
            throw new RuntimeException("Version node not found in pom.xml.")
        }

        pomFile.withWriter("UTF-8") { writer ->
            XmlUtil.serialize(pom, writer)
        }
        
        println "Updated version to ${buildNumber} in pom.xml"
    }
        
}




def startBuild (String imageName = "maven:3.9.8-amazoncorretto-11") {
        docker.image(imageName).pull()
        docker.image(imageName).inside() {
            sh "mvn clean package"
    }
 }

return this 
