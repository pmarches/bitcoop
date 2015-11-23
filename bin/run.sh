bcoopPath=`dirname $0`/..
jarlibs=`find $bcoopPath/lib -name "*.jar" | awk '{printf "%s:", $1} END {print "."}' bcoopPath=$bcoopPath`

cd $bcoopPath
java -cp classes:$jarlibs bcoop.server.BCoopServer bcoop.server.ourPeerId=`hostname -s`
