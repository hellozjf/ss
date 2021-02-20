#!/bin/sh
# 工作目录处理
cdir=$(cd `dirname $0`; pwd)
cd $cdir
workdir=$(cd ..;pwd)
cd $workdir

pid=${workdir}"/bin/run.pid"
echo 'try to stop app '`cat $pid`
if [ -f "$pid" ]; then
    kill -9 `cat $pid`
    rm $pid
    echo 'app already stoped! '
else
    echo 'pid file not found! '
fi