# Sistema de Gestão de Projetos

## Funcionalidades

- Cadastro de Usuários, Projetos, Equipes, Tarefas
- Controle de Permissões por Perfil (Admin/Gerente/Colaborador)
- Relatórios: Dashboard, Desempenho, Risco de Atraso
- Validações: CPF, Email, Datas

## Executar

1. Configure o banco MySQL
2. Execute: `java -cp "bin;lib/mysql-connector-j-9.4.0.jar" view.LoginFrame`
3. Login: admin/admin123

## Compilar

### Opção 1 compilar: Script automático

.\compilar.bat
java -cp "bin;lib/mysql-connector-j-9.4.0.jar" view.LoginFrame

### Opção 2 compilar: Manual

javac -cp ".;lib/mysql-connector-j-9.4.0.jar" -d bin src/_.java src/model/_.java src/dao/_.java src/util/_.java src/view/\*.java
java -cp "bin;lib/mysql-connector-j-9.4.0.jar" view.LoginFrame

## Tecnologias

- Java Swing
- MySQL
- JDBC
