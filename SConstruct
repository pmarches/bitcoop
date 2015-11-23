import os
env = Environment(ENV = os.environ)

env['JAVACFLAGS'] = '-classpath lib/ant-junit.jar:lib/cobertura.jar:lib/hsqldb.jar:lib/junit.jar:lib/local_policy.jar:lib/log4j-1.2.12.jar'

env.Java('classes', ['src','lib'])

SConscript('native/SConstruct')
