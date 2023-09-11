pipeline {   
    //agent any
    agent{
        kubernetes {
          cloud 'ns-agent' // 在jenkins中可以配置多个集群， 在这里指定集群名称
          yaml '''
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: busybox
    image: busybox:latest
    command: ["sleep", "3600"]  # 示例命令，这里使用 sleep 命令来保持容器运行
    tty: true 
  - name: maven
    image: maven:3.6.3-jdk-11-slim
    command: ["sleep", "3600"]  # 示例命令，这里使用 sleep 命令来保持容器运行
    tty: true
'''

  //   volumeMounts:
  //   - mountPath: "/root/.m2/"
  //     name: "cachedir"
  //     readOnly: false    
  // volumes:
  //   - hostPath:
  //       path: "/var/run/docker.sock"
  //     name: "dockersock"      
  //   - name: "cachedir"
  //     persistentVolumeClaim:
  //       claimName: maven-pvc
        }
    } 
    stages {
        stage('building'){
            steps{
                container(name: 'busybox'){
                    sh"""
                      ping -c 5 www.baidu.com
                    """
                }
                container(name: 'maven'){
                    sh"""
                      mvn clean test -X -Dmaven.repo.remote=https://maven.aliyun.com/repository/public
                      ls -last
                    """
                }
            }
        }       
    }
    post {                
        // If Maven was able to run the tests, even if some of the test
        // failed, record the test results and archive the jar file.
        success { allure([
            includeProperties: false,
            jdk: '',
            properties: [],
            reportBuildPolicy: 'ALWAYS',
            results: [[path: 'allure-results']]
        ])
        }
    }

}
