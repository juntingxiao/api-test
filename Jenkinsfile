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
  - name: maven
    image: maven:3.6.3-jdk-11-slim
    command: ["sleep", "3600"]  # 示例命令，这里使用 sleep 命令来保持容器运行
    tty: true
    volumeMounts:
    - mountPath: "/root/.m2/"
      name: "cachedir"
      readOnly: false    
  volumes:
    - hostPath:
        path: "/var/run/docker.sock"
      name: "dockersock"      
    - name: "cachedir"
      persistentVolumeClaim:
        claimName: maven-pvc
'''
        }
    } 
    stages {
        stage('building'){
            steps{
                container(name: 'maven'){
                    sh"""
                      ping -c 5 www.baidu.com
                      mvn clean test -X -Dmaven.repo.remote=https://maven.aliyun.com/repository/public
                      ls -last
                      ping -c 5 www.baidu.com
                    """
                }
            }
        }       
    }
}
