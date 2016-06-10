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
	high  decimal(10,2) not null,
	low  decimal(10,2) not null,
	close decimal(10,2) not null,
	open decimal(10,2) not null,
	volume decimal(15,2) not null,
	primary key (stock_history_id)

) ENGINE=InnoDB;
alter table stock_history add constraint fk_stockhistory_stock foreign key (stock_id) references stock (stock_id);
alter table stock_history add unique index (stock_id, date);


create table account (
	account_id BIGINT NOT NULL AUTO_INCREMENT,
	balance decimal(15,2) not null,
	initial_position decimal(15,2) not null,
	primary key (account_id)
) ENGINE=InnoDB;

create table account_analyse_stock (
	account_id BIGINT NOT NULL,
	stock_id INT NOT NULL,
	primary key (account_id, stock_id)
) ENGINE=InnoDB;
alter table account_analyse_stock add constraint fk_account_analyse_stock_stock foreign key (stock_id) references stock (stock_id);
alter table account_analyse_stock add constraint fk_account_analyse_stock_account foreign key (account_id) references account (account_id);

create table model (
	model_id BIGINT NOT NULL AUTO_INCREMENT,
	account_id BIGINT NOT NULL,
	dat_start DATE NOT NULL,
	dat_end DATE NOT NULL,
	primary key (model_id)
) ENGINE=InnoDB;
alter table model add constraint fk_donchian_model_account foreign key (account_id) references account (account_id);

create table donchian_model_entry (
	donchian_model_entry_id BIGINT NOT NULL AUTO_INCREMENT,
	model_id BIGINT NOT NULL,
	stock_id INT NOT NULL,
	entry_size INT NOT NULL,
	exit_size INT NOT NULL,
	risk_rate decimal(5,2) not null,
	primary key (donchian_model_entry_id)
) ENGINE=InnoDB;
alter table donchian_model_entry add unique index (model_id, stock_id);
alter table donchian_model_entry add constraint fk_donchian_model_entry_stock foreign key (stock_id) references stock (stock_id);
alter table donchian_model_entry add constraint fk_donchian_model_entry_model foreign key (model_id) references model (model_id);

create table op_order (
	order_id BIGINT NOT NULL AUTO_INCREMENT,
	type ENUM('B', 'S') NOT NULL,
	stock_id INT NOT NULL,
	model_id BIGINT,
	size decimal(10, 2) not null,
	creation_date DATE NOT NULL,
	value decimal(10, 2) not null,
	stop_pos decimal(10, 2) not null,
	primary key (order_id)
) ENGINE=InnoDB;
alter table op_order add constraint fk_order_stock foreign key (stock_id) references stock (stock_id);
alter table op_order add constraint fk_order_model foreign key (model_id) references model (model_id);

create table wallet (
  wallet_id BIGINT NOT NULL AUTO_INCREMENT,
  stock_id INT NOT NULL,
  account_id BIGINT NOT NULL,
  size decimal(15,2) not null,
  dat_creation DATETIME NOT NULL,
  dat_update DATETIME NOT NULL,
  primary key (wallet_id),
  unique key (account_id, stock_id)
) ENGINE=InnoDB;
alter table wallet add constraint fk_wallet_stock foreign key (stock_id) references stock (stock_id);
alter table wallet add constraint fk_wallet_account foreign key (account_id) references account (account_id);

create table operation (
	operation_id BIGINT NOT NULL AUTO_INCREMENT,
	order_id BIGINT NOT NULL,
	dat_creation DATETIME NOT NULL,
	size decimal(15,2) not null,
	value decimal(15,2) not null,
	stop_loss decimal(10,2) not null,
	primary key (operation_id)
) ENGINE=InnoDB;
alter table operation add constraint fk_operation_order_id foreign key (order_id) references op_order (order_id);
