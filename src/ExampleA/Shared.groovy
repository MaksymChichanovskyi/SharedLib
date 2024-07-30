package ExampleA

def CheckoutGit(){
        checkout changelog: false, scm: [$class: 'GitSCM', branches: [[name: '*/main']],
                                             extensions       : [[$class: 'LocalBranch', localBranch: '**'], [$class: 'UserIdentity', name: 'Jenkins']],
                                             userRemoteConfigs: [[credentialsId: 'cf84bbaf-792c-4bac-98ae-b80958b2656f', refspec: '+refs/heads/main:refs/remotes/origin/main',
                                             url: 'https://github.com/MaksymChichanovskyi/MavenProject.git']]]
}
def call() {
    def buildNumber = env.BUILD_NUMBER
    echo "Current Build Number: ${buildNumber}"

    def pomFile = new File('pom.xml')
    if (pomFile.exists()) {
        def pom = new XmlParser().parse(pomFile)
        def versionNode = pom.version

        if (versionNode) {
            def newVersion = "${buildNumber}"
            versionNode[0].value = newVersion
            def writer = new StringWriter()
            new XmlNodePrinter(new PrintWriter(writer)).print(pom)
            pomFile.text = XmlUtil.serialize(pom)
            echo "Updated version in pom.xml to ${newVersion}"
        } else {
            echo "No version node found in pom.xml"
        }
    } else {
        error "pom.xml not found"
    }
}
}






def startBuild (String imageName = "maven:3.9.8-amazoncorretto-11") {
        docker.image(imageName).pull()
        docker.image(imageName).inside() {
            sh "mvn clean package"
    }
 }

return this 
