/*
pay_log 2 foreign keys
and foreign for alot of attrebutes

tables
attrebutes
relations 
cardnalities

insert into [table name] (attre1,attre2,atter3,....) values(attre1v,attre2v,atter3v,....)

*/


-------------<<<<<<<< creation part >>>>>>>>>>>---------------
create table department(
    id int primary key,
    name varchar(25) ,
    manger_id int unique,
    number_of_employees int
)
create table branch(
    id int primary key,
    location varchar(25), 
    manger_id int unique,
    number_of_employees int not null,
    number_of_department int not null,
    assets float    
)
create table employees(
    id int primary key,
    name varchar(25) ,
    address varchar(25),
    phone_number varchar(25),
    gender varchar(25),
    age int ,
    email varchar(25),
    edu_degree varchar(25),
    manger_id int,
    department int,
    branch int
    )
    create table full_time(
        id int primary key,
        salary float ,
        shift varchar(25),
        constraint c6 foreign key (id) references employees(id)
    )
         create table manger(
            id int primary key,
            position int ,
            constraint c7 foreign key (id) references full_time(id)
        )
    create table part_time(
        id int primary key,
        hour_salary float, 
        work_hours int,
        constraint c8 foreign key (id) references employees(id)
    )


alter table department add constraint c1 foreign key (manger_id) references manger(id)
alter table employees add constraint c2 foreign key (manger_id) references employees(id)
alter table employees add constraint c3 foreign key (department) references department(id)
alter table employees add constraint c4 foreign key (branch) references branch(id)
alter table branch add constraint c5 foreign key (manger_id) references manger(id)

create table customers(
    id int primary key,
    name varchar(25) ,
    address varchar(25),
    phone_number varchar(25),
    gender varchar(25),
    age int ,
    email varchar(25)
)
create table currencies(
    id int primary key,
    name varchar(25),
    one_gram_of_GOLD float ,
    sell_price float,
    buy_price float
)
create table accounts(
    account_number varchar(25) primary key,
    owner int not null ,  /*   customers_id   */ 
    type varchar(25),
    balance float ,
    date_created timestamp default sysdate not null,
    currency_id int not null,
    branch_HandleIt int not null, 
    constraint c9 foreign key (owner) references customers(id),
    constraint c10 foreign key (currency_id) references currencies(id),
    constraint c11 foreign key (branch_HandleIt) references branch(id)
)
create table atm(
    id int primary key,
    location varchar(25),
    assets float ,
    type varchar(25)
)
create table loan(
    loan_id int primary key,
    type varchar(25),
    min_value float,
    max_value float,
    benefits float ,
    installment_period int 
)
create table loan_certificate(
    loan_id int not null,
    customers_id int not null,
    approved_by int not null ,
    loan_amount float ,
    borrower_type varchar(25),
    start_date timestamp default sysdate not null,
    end_date timestamp default sysdate not null,
    payed float ,
    remained_amount float, 
    constraint c18 foreign key (loan_id) references loan(loan_id),
    constraint c19 foreign key (customers_id) references customers(id),
    constraint c20 foreign key (approved_by) references manger(id),
    constraint c21 primary key (loan_id,customers_id,start_date)
)
create table pay_log(
    credit_id varchar(25) not null,
    repay_date timestamp default sysdate not null,
    credit_amount float,
    constraint c22 primary key (credit_id,repay_date)
)
create table loan_pay_log(
    credit_id varchar(25) not null,
    repay_date timestamp default sysdate not null,
    loan_id int not null,
    customers_id int not null,
    start_date timestamp default sysdate not null,
    constraint c23 primary key (credit_id,repay_date),
    constraint c24 foreign key (loan_id,customers_id,start_date) references loan_certificate(loan_id,customers_id,start_date),
    constraint c25 foreign key (credit_id,repay_date) references pay_log(credit_id,repay_date)
)
create table card(
    card_number varchar(25) primary key,
    network varchar(25),
    cvv varchar(25),
    expire_date timestamp default sysdate not null,
    valid_amount float,
    belongs_to varchar(25) not null unique ,
    owner int not null ,
    constraint c12 foreign key (owner) references customers(id),
    constraint c13 foreign key (belongs_to) references accounts(account_number)
)  
create table credit_card(
    card_number varchar(25) primary key,
    benefits float ,
    start_pay_date timestamp default sysdate not null,
    end_pay_date timestamp default sysdate not null,
    pay_amount float ,
    constraint c17 foreign key (card_number) references card(card_number)
)
create table transaction(
    atm_id int not null ,
    card_number varchar(25) not null,
    type varchar(25),
    transaction_date timestamp default sysdate not null,
    value float,
    constraint c14 foreign key (card_number) references card(card_number),
    constraint c15 foreign key (atm_id) references atm(id),
    constraint c16 primary key (atm_id,card_number,transaction_date)
)
create table credit_card_pay_log(
    credit_id varchar(25) not null,
    repay_date timestamp default sysdate not null,
    card_number varchar(25) ,
    constraint c26 primary key (credit_id,repay_date),
    constraint c27 foreign key (credit_id,repay_date) references pay_log(credit_id,repay_date),
    constraint c28 foreign key (card_number) references credit_card(card_number)
)


/*
natural join 
select c.id ,c.name
inner join
select c.id, c.name,c.email,a.ACCOUNT_NUMBER,a.balance,a.DATE_CREATED from CUSTOMERS c join ACCOUNTS a on (c.id = a.owner)
*/