import pika
import pymysql
import time

# 设置连接数据库的参数
db_params = {
    'host': 'localhost',
    'user': 'root',
    'password': 'qixinkai123',
    'db': 'computerdesign',
    'charset': 'utf8mb4',
    'cursorclass': pymysql.cursors.DictCursor
}


def connect_rabbitmq():
    """尝试连接到RabbitMQ服务器，失败则重试。"""
    while True:
        try:
            rabbit_params = pika.ConnectionParameters(host='localhost')
            return pika.BlockingConnection(rabbit_params)
        except pika.exceptions.ConnectionWrongStateError:
            print("Failed to connect to RabbitMQ. Retrying...")
            time.sleep(2)  # 等待2秒后重试


# 查询event_log表并获取事件
def fetch_events(cursor):
    cursor.execute("SELECT event_id, table_name FROM event_log ORDER BY action_time DESC LIMIT 10")
    return cursor.fetchall()


# 将事件通知发送到RabbitMQ
def send_to_rabbitmq(channel, routing_key, message):
    channel.basic_publish(
        exchange='table_changes_exchange',
        routing_key=routing_key,
        body=message
    )
    print(f"Sent '{message}' to exchange 'table_changes_exchange' with routing key '{routing_key}'")


# 主函数
def main():
    # 设置数据库连接
    db_connection = pymysql.connect(**db_params)
    cursor = db_connection.cursor()

    # 设置RabbitMQ连接
    rabbit_connection = connect_rabbitmq()
    channel = rabbit_connection.channel()

    try:
        while True:
            # 获取事件
            events = fetch_events(cursor)
            for event in events:
                table_name = event['table_name']
                if table_name == 'drive_type':
                    routing_key = 'drive_type_key'
                elif table_name == 'objects':
                    routing_key = 'objects_key'
                elif table_name == 'traffic_signal':
                    routing_key = 'traffic_signal_key'
                else:
                    continue

                message = str(1)
                send_to_rabbitmq(channel, routing_key, message)
                cursor.execute("DELETE FROM event_log WHERE event_id = %s", (event['event_id'],))

            # 提交更改并等待一段时间
            db_connection.commit()
            time.sleep(1)  # Polling interval
    except Exception as e:
        print(f"An error occurred: {e}")
    finally:
        db_connection.close()
        rabbit_connection.close()


if __name__ == "__main__":
    main()
