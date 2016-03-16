create database stock_analysis;
use stock_analysis;

create table stock (
	stock_id INT NOT NULL AUTO_INCREMENT,
	stock_code varchar(10) NOT NULL,
	stock_name varchar(40) NOT NULL,
	primary key (stock_id)
) ENGINE=InnoDB;
alter table stock add unique index (stock_code);

create table stock_history (
	stock_history_id BIGINT NOT NULL AUTO_INCREMENT,
	stock_id INT NOT NULL,
	date DATE NOT NULL,
	close decimal(10,2) not null,
	open decimal(10,2) not null,
	volume decimal(15,2) not null,
	primary key (stock_history_id)

) ENGINE=InnoDB;
alter table stock_history add constraint fk_stockhistory_stock foreign key (stock_id) references stock (stock_id);
alter table stock_history add unique index (stock_id, date);
