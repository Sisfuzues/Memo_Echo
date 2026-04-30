# 机器人部署说明

这份文档只保留 `bot_gateway` 和 NapCat 的最小部署步骤。  
项目总览、模块说明和联调入口请先看根目录 [`readme.md`](/home/zhouyifei/Java_Project/Memo_Echo/readme.md)。

## NapCat 本地启动

```yaml
services:
  napcat:
    image: mlikiowa/napcat-docker:latest
    container_name: memo_echo_napcat
    environment:
      - WEBHOOK_URL=http://localhost:8080/api/bot/webhook
    volumes:
      - ./napcat/config:/app/napcat/config
      - ./napcat/qq:/app/.config/QQ
    ports:
      - "6099:6099"
      - "3011:3011"
    restart: always
```

启动：

```bash
sudo docker compose up -d
```

查看日志：

```bash
sudo docker logs --tail 50 memo_echo_napcat
```

NapCat WebUI：

- `http://127.0.0.1:6099/webui/`

## 注意事项

- webhook 要指向 `bot_gateway` 的 `/api/bot/webhook`
- RocketMQ NameServer 默认是 `127.0.0.1:9876`
- `bot_gateway` 默认端口是 `8080`

## 相关文档

- 根 README: [`readme.md`](/home/zhouyifei/Java_Project/Memo_Echo/readme.md)
- 接口规范: [`memo_echo_apis/APIS规范.md`](/home/zhouyifei/Java_Project/Memo_Echo/memo_echo_apis/APIS规范.md)
