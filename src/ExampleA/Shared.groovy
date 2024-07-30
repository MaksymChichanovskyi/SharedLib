package ExampleA


    def defaultCheckout() {
        checkout(scm)
    }

    def startBuild(String imageName = "maven:3.9.8-amazoncorretto-11") {
        docker.image(imageName).pull()
        docker.image(imageName).inside() {
            sh "mvn clean package"
        }
    }

    def updatePomVersion(String buildNumber) {
        def pomFile = readFile 'pom.xml'
        def updatedPomFile = pomFile.replaceAll('<version>1.0-SNAPSHOT</version>', "<version>1.0.${buildNumber}</version>")
        writeFile file: 'pom.xml', text: updatedPomFile
        echo "Updated pom.xml with build number: ${buildNumber}"
    }

    def getJarSize() {
        def jarFile = findFiles(glob: 'target/*.jar')[0]
        def jarSize = new File(jarFile.path).length()
        def jarSizeMB = jarSize / (1024 * 1024)
        echo "JAR File: ${jarFile.path}, Size: ${jarSizeMB} MB"
    }
return this
