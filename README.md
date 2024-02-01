# Projeto Java LDAP 💻

``` Escopo:```

✅ Conexão Java com LDAP Apache Directory

## 📋 Classes utilizadas

📌 Config
📌 Usuario
📌 Grupo
📌 Main

-----------------------------------------------------------------

## ⏯ Inicializando o Sistema

#### Verifique se tem o Java 17 instalado
#### Utilizar IDE de preferência(Eclipse, VSCode ou outra compatível)
###### Para executar o programa basta executar a classe Main.java

-----------------------------------------------------------------

### 🙍‍♂️ Classe Usuário

```1. addUser ``` <br/>

Este método adiciona um novo usuário na base LDAP.

```2. addUserToGroup ``` <br/>

Este método adiciona um usuário em um grupo existente na base LDAP.

```3. addFromXML``` <br/>

Este método recebe um arquivo XML e realiza as operações de criação de usuário na base LDAP.

```4. groupExists``` <br/>

Este método verifica se determinado grupo existe na base LDAP.

```5. modifyXML``` <br/>

Este método recebe um arquivo XML e realiza as operações de modificação de usuário na base LDAP.

```6. removeUserFromGroup``` <br/>

Este método remove um usuário em um grupo existente na base LDAP.

-----------------------------------------------------------------

### 🧩 Classe Grupo

```1. addGroup ``` <br/>

Este método adiciona um novo grupo na base LDAP.

```2. addFromXML``` <br/>

Este método recebe um arquivo XML e realiza as operações de criação de grupo na base LDAP.

-----------------------------------------------------------------

### ⚙️ Classe Config

```1. newConnection ``` <br/>

Este método cria uma conexão com a base LDAP.

```2. closeConnection``` <br/>

Este método encerra uma conexão com a base LDAP.

-----------------------------------------------------------------

### ▶️ Classe Main

```1. Conexão com ldap ``` <br/>

Inicialmente é estabelecida a conexão de um usuário na base LDAP.

```2. Criação dos grupos ``` <br/>

Em seguida é criado os devidos grupos na na base LDAP de acordo com os arquivos XML de entrada.

```3. Criação dos usuários``` <br/>

Logo após é criado os devidos usuários na na base LDAP de acordo com os arquivos XML de entrada.

```4. Modificação do usuário``` <br/>

O usuário criado tem alterações nos seus grupos pertencentes na base LDAP.

```5. Conexão fechada``` <br/>

Por fim, a conexão na base LDAP é encerrada.

-----------------------------------------------------------------

## 🖥️ Tecnologias

IDE: Eclipse

Java 17 <br/>
XPath <br/>
LDAP <br/>
Apache Directory <br/>
Maven

