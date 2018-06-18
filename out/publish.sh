mysql -u root -padmin <<EOF
    create database IF NOT EXISTS taglibdatabase charset utf8;
EOF
nohup java -jar taglib-0.0.1-SNAPSHOT.jar &
