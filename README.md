# Cliente Github - etiquetas para os repositórios

Esta aplicação web permite que você coloque etiquetas nos repositórios que você colocou estrela.

## Como executar este aplicativo

Os passos a seguir podem instalar o SDK do Google App Engine automaticamente, mas talvez você precise instalar o SDK manualmente se ocorrer algum problema no seu sistema.

### Como executar localmente

Para executar esta aplicação localmente:

```
./gradle appengineRun
```

### Como subir o banco de dados localmente

Para executar esta aplicação localmente:

```
gcloud beta emulators datastore env-init > set_vars.cmd && set_vars.cmd
gcloud beta emulators datastore start
```

Para ativar o hot-reload, execute isso em outro terminal enquanto a aplicação estiver rodando:

```
./gradle -t build
```

### Como subir o banco de dados localmente

```
gcloud beta emulators datastore env-init > set_vars.cmd && set_vars.cmd
gcloud beta emulators datastore start
```

Para que os dados não sejam persistidos em disco:

```
gcloud beta emulators datastore start --no-store-on-disk
```

### Como executar testes

Para executar os testes dessa aplicação:

```
./gradle test
```

### Como realizar o deploy

Para fazer o deploy na sua conta Google:

```
./gradle appengineDeploy
```

