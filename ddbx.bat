call mvnw install -f pom.xml
call del H:\�쳵��\SVN������2\kjb\ddbxm.war
call move G:\gitHub\ddbx\target\ddbx-0.0.1-SNAPSHOT.war H:\�쳵��\SVN������2\kjb\ddbxm.war
h:
call cd \�쳵��\SVN������2\kjb\
call svn ci -m "ddbx" ddbxm.war
g:
call cd g:\gitHub\ddbx\