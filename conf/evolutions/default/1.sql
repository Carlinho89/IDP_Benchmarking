# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table input (
  id                        varchar(255) not null,
  name                      varchar(255),
  type                      varchar(255),
  constraint pk_input primary key (id))
;

create table seasonal_data (
  id                        varchar(255) not null,
  team_id                   varchar(255),
  team_name                 varchar(255),
  year                      varchar(255),
  league_id                 varchar(255),
  input_id                  varchar(255),
  value                     varchar(255),
  constraint pk_seasonal_data primary key (id))
;

create table team (
  id                        varchar(255) not null,
  name                      varchar(255),
  transfermarkt_name        varchar(255),
  logo                      varchar(255),
  constraint pk_team primary key (id))
;

create table transfermarkt_team (
  id                        varchar(255) not null,
  name                      varchar(255),
  position                  varchar(255),
  year                      varchar(255),
  league_id                 varchar(255),
  constraint pk_transfermarkt_team primary key (id))
;

create sequence input_seq;

create sequence seasonal_data_seq;

create sequence team_seq;

create sequence transfermarkt_team_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists input;

drop table if exists seasonal_data;

drop table if exists team;

drop table if exists transfermarkt_team;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists input_seq;

drop sequence if exists seasonal_data_seq;

drop sequence if exists team_seq;

drop sequence if exists transfermarkt_team_seq;

