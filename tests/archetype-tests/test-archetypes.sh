localDir="$(readlink -f $0)"
installDir="$(dirname "${localDir}")"/../..

WANAKU_VERSION=$(cat ${installDir}/capabilities-common/target/classes/version.txt)


workDir=$(pwd)

if [[ -d build ]] ; then
  rm -rf build
fi

mkdir build && cd build


mvn -B archetype:generate -DarchetypeGroupId=ai.wanaku.sdk \
  -DarchetypeArtifactId=capabilities-archetypes-java-tool \
  -DarchetypeVersion=${WANAKU_VERSION} \
  -DgroupId=ai.test \
  -Dpackage=ai.test \
  -DartifactId=test \
  -Dname=Test \
  -Dwanaku-sdk-version=${WANAKU_VERSION}

if [[ ! -d Test ]] ; then
  echo "The generated directory is wrong"
  ls -1
  exit 1
else
  cd test
fi

mvn clean package
if [[ $? -ne 0 ]] ; then
  echo "The code does not compile"
  exit 2
fi

cd "${workDir}"