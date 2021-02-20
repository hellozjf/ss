#!/bin/sh
# 工作目录处理
cdir=$(cd `dirname $0`; pwd)
cd $cdir
workdir=$(cd ..;pwd)
cd $workdir
# 输出app工作目录位置
echo "App WorkDir is: [${workdir}]"

# 启动应用，easyweb-ai应用因为要加载相似度计算的词汇向量，而词汇向量有645MB，所以我们要加大内存到2GB
java -Xms128m -Xmx128m -Xlog:gc -jar jar/shadowsocks.jar --spring.profiles.active=test >log/ss.log 2>&1