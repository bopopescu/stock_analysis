delete from trade;
delete from op_order;
delete from donchian_model_entry;
delete from model;
delete from account_analyse_stock;
delete from account;

insert into account (balance, initial_position) values (10000,10000);
update account set account_id=1;

insert into account_analyse_stock (account_id, stock_id) values ((select max(account_id) from account), (select stock_id from stock where stock_code='BBDC4'));
insert into account_analyse_stock (account_id, stock_id) values ((select max(account_id) from account), (select stock_id from stock where stock_code='BDLL4'));
insert into account_analyse_stock (account_id, stock_id) values ((select max(account_id) from account), (select stock_id from stock where stock_code='BGIP4'));
insert into account_analyse_stock (account_id, stock_id) values ((select max(account_id) from account), (select stock_id from stock where stock_code='BOBR4'));
insert into account_analyse_stock (account_id, stock_id) values ((select max(account_id) from account), (select stock_id from stock where stock_code='BRAP4'));
insert into account_analyse_stock (account_id, stock_id) values ((select max(account_id) from account), (select stock_id from stock where stock_code='BRIV4'));
insert into account_analyse_stock (account_id, stock_id) values ((select max(account_id) from account), (select stock_id from stock where stock_code='CMIG4'));
insert into account_analyse_stock (account_id, stock_id) values ((select max(account_id) from account), (select stock_id from stock where stock_code='CRIV4'));
insert into account_analyse_stock (account_id, stock_id) values ((select max(account_id) from account), (select stock_id from stock where stock_code='CTNM4'));
insert into account_analyse_stock (account_id, stock_id) values ((select max(account_id) from account), (select stock_id from stock where stock_code='ELPL4'));
insert into account_analyse_stock (account_id, stock_id) values ((select max(account_id) from account), (select stock_id from stock where stock_code='ESTR4'));
insert into account_analyse_stock (account_id, stock_id) values ((select max(account_id) from account), (select stock_id from stock where stock_code='FJTA4'));
insert into account_analyse_stock (account_id, stock_id) values ((select max(account_id) from account), (select stock_id from stock where stock_code='GETI4'));
insert into account_analyse_stock (account_id, stock_id) values ((select max(account_id) from account), (select stock_id from stock where stock_code='GGBR4'));
insert into account_analyse_stock (account_id, stock_id) values ((select max(account_id) from account), (select stock_id from stock where stock_code='GOAU4'));
insert into account_analyse_stock (account_id, stock_id) values ((select max(account_id) from account), (select stock_id from stock where stock_code='GOLL4'));
insert into account_analyse_stock (account_id, stock_id) values ((select max(account_id) from account), (select stock_id from stock where stock_code='GUAR4'));
insert into account_analyse_stock (account_id, stock_id) values ((select max(account_id) from account), (select stock_id from stock where stock_code='INEP4'));
insert into account_analyse_stock (account_id, stock_id) values ((select max(account_id) from account), (select stock_id from stock where stock_code='ITSA4'));
insert into account_analyse_stock (account_id, stock_id) values ((select max(account_id) from account), (select stock_id from stock where stock_code='LAME4'));
insert into account_analyse_stock (account_id, stock_id) values ((select max(account_id) from account), (select stock_id from stock where stock_code='LIXC4'));
insert into account_analyse_stock (account_id, stock_id) values ((select max(account_id) from account), (select stock_id from stock where stock_code='MGEL4'));
insert into account_analyse_stock (account_id, stock_id) values ((select max(account_id) from account), (select stock_id from stock where stock_code='MTSA4'));
insert into account_analyse_stock (account_id, stock_id) values ((select max(account_id) from account), (select stock_id from stock where stock_code='MWET4'));
insert into account_analyse_stock (account_id, stock_id) values ((select max(account_id) from account), (select stock_id from stock where stock_code='PCAR4'));
insert into account_analyse_stock (account_id, stock_id) values ((select max(account_id) from account), (select stock_id from stock where stock_code='PETR4'));
insert into account_analyse_stock (account_id, stock_id) values ((select max(account_id) from account), (select stock_id from stock where stock_code='POMO4'));
insert into account_analyse_stock (account_id, stock_id) values ((select max(account_id) from account), (select stock_id from stock where stock_code='RAPT4'));
insert into account_analyse_stock (account_id, stock_id) values ((select max(account_id) from account), (select stock_id from stock where stock_code='RCSL4'));
insert into account_analyse_stock (account_id, stock_id) values ((select max(account_id) from account), (select stock_id from stock where stock_code='SAPR4'));
insert into account_analyse_stock (account_id, stock_id) values ((select max(account_id) from account), (select stock_id from stock where stock_code='SHUL4'));
insert into account_analyse_stock (account_id, stock_id) values ((select max(account_id) from account), (select stock_id from stock where stock_code='SLED4'));
insert into account_analyse_stock (account_id, stock_id) values ((select max(account_id) from account), (select stock_id from stock where stock_code='TEKA4'));
insert into account_analyse_stock (account_id, stock_id) values ((select max(account_id) from account), (select stock_id from stock where stock_code='TOYB4'));
insert into account_analyse_stock (account_id, stock_id) values ((select max(account_id) from account), (select stock_id from stock where stock_code='TRPL4'));

insert into model (account_id, dat_start, dat_end)
  values (
    (select max(account_id) from account),
    '2014-02-01',
    '2014-05-31'
  );

  insert into donchian_model_entry (model_id, stock_id, entry_size, exit_size, risk_rate)
    values (
      (select max(model_id) from model),
      (select stock_id from stock where stock_code='CMIG4'),
      10, 2, 0.03
    );

  insert into donchian_model_entry (model_id, stock_id, entry_size, exit_size, risk_rate)
    values (
      (select max(model_id) from model),
      (select stock_id from stock where stock_code='POMO4'),
      10, 2, 0.03
    );

insert into model (account_id, dat_start, dat_end)
  values (
    (select max(account_id) from account),
    '2014-06-01',
    '2014-09-30'
  );

  insert into donchian_model_entry (model_id, stock_id, entry_size, exit_size, risk_rate)
    values (
      (select max(model_id) from model),
      (select stock_id from stock where stock_code='BBDC4'),
      10, 2, 0.03
    );

  insert into donchian_model_entry (model_id, stock_id, entry_size, exit_size, risk_rate)
    values (
      (select max(model_id) from model),
      (select stock_id from stock where stock_code='PETR4'),
      10, 2, 0.03
    );

  insert into donchian_model_entry (model_id, stock_id, entry_size, exit_size, risk_rate)
    values (
      (select max(model_id) from model),
      (select stock_id from stock where stock_code='SLED4'),
      10, 2, 0.03
    );

  insert into donchian_model_entry (model_id, stock_id, entry_size, exit_size, risk_rate)
    values (
      (select max(model_id) from model),
      (select stock_id from stock where stock_code='TRPL4'),
      10, 10, 0.03
    );

insert into model (account_id, dat_start, dat_end)
  values (
    (select max(account_id) from account),
    '2014-10-01',
    '2014-12-31'
  );

  insert into donchian_model_entry (model_id, stock_id, entry_size, exit_size, risk_rate)
    values (
      (select max(model_id) from model),
      (select stock_id from stock where stock_code='GETI4'),
      10, 2, 0.03
    );

  insert into donchian_model_entry (model_id, stock_id, entry_size, exit_size, risk_rate)
    values (
      (select max(model_id) from model),
      (select stock_id from stock where stock_code='GOLL4'),
      10, 2, 0.03
    );

  insert into donchian_model_entry (model_id, stock_id, entry_size, exit_size, risk_rate)
    values (
      (select max(model_id) from model),
      (select stock_id from stock where stock_code='TRPL4'),
      10, 2, 0.03
    );
