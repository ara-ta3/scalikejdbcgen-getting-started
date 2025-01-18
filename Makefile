CONTAINER_NAME = scalikejdbcgen-gettingstarted
MYSQL_ROOT_PASSWORD = rootpassword
MYSQL_DATABASE = ecsite-samples
MYSQL_USER = user
MYSQL_PASSWORD = password
MYSQL_IMAGE = mysql:8.0

start:
	docker rm -f $(CONTAINER_NAME)
	docker run --name $(CONTAINER_NAME) \
		-e MYSQL_ROOT_PASSWORD=$(MYSQL_ROOT_PASSWORD) \
		-e MYSQL_DATABASE=$(MYSQL_DATABASE) \
		-e MYSQL_USER=$(MYSQL_USER) \
		-e MYSQL_PASSWORD=$(MYSQL_PASSWORD) \
		-v $(PWD)/migrations:/docker-entrypoint-initdb.d \
		-p 3306:3306 $(MYSQL_IMAGE)

wait-for-mysql:
	until docker exec $(CONTAINER_NAME) mysqladmin ping -u$(MYSQL_USER) -p$(MYSQL_PASSWORD) --silent; do \
		echo "Waiting for MySQL to be ready..."; \
		sleep 10; \
	done

stop:
	docker stop $(CONTAINER_NAME)

clean: stop
	docker rm $(CONTAINER_NAME)

mysqlcli:
	docker exec -it $(CONTAINER_NAME) mysql -u$(MYSQL_USER) -p$(MYSQL_PASSWORD) $(MYSQL_DATABASE)

