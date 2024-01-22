import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.ideaRunner
import jetbrains.buildServer.configs.kotlin.projectFeatures.awsConnection
import jetbrains.buildServer.configs.kotlin.projectFeatures.s3Storage
import jetbrains.buildServer.configs.kotlin.triggers.vcs

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2023.11"

project {

    buildType(Build)

    features {
        awsConnection {
            id = "AmazonWebServicesAws"
            name = "Amazon Web Services (AWS)"
            credentialsType = static {
                accessKeyId = "AKIA5JH2VERVJFGDHSDZ"
                secretAccessKey = "credentialsJSON:c7bed6d8-8e41-449d-ae0d-6cdf2a5c4296"
            }
            param("id", "AmazonWebServicesAws")
        }
        s3Storage {
            id = "PROJECT_EXT_4"
            awsEnvironment = default {
            }
            connectionId = "AmazonWebServicesAws"
            storageName = "new storage"
            bucketName = "ollven.test.2"
            forceVirtualHostAddressing = true
        }
    }
}

object Build : BuildType({
    name = "Build"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        ideaRunner {
            id = "JPS"
            pathToProject = ""
            jdk {
                name = "17"
                path = "%env.JDK_17_0%"
                patterns("jre/lib/*.jar", "jre/lib/ext/jfxrt.jar")
                extAnnotationPatterns("%teamcity.tool.idea%/lib/jdkAnnotations.jar")
            }
            jvmArgs = "-Xmx256m"
            coverageEngine = idea {
                includeClasses = "HelloWorldIdea.iml"
            }
        }
        maven {
            id = "Maven2"
            enabled = false
            goals = "clean test"
            pomLocation = ".teamcity/pom.xml"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
            coverageEngine = idea {
                includeClasses = "de.ollven.*"
            }
        }
    }

    triggers {
        vcs {
        }
    }

    features {
        perfmon {
        }
    }
})
