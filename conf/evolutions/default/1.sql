# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table fixed_data (
  id                        integer not null,
  team_id                   integer,
  team_name                 varchar(255),
  league_id                 integer,
  input_id                  integer,
  value                     float,
  constraint pk_fixed_data primary key (id))
;

create table input (
  id                        integer not null,
  name                      varchar(255),
  type                      varchar(255),
  output                    boolean,
  value_type                varchar(255),
  constraint pk_input primary key (id))
;

create table league (
  id                        integer not null,
  name                      varchar(255),
  team_number               integer,
  logo                      varchar(255),
  constraint pk_league primary key (id))
;

create table seasonal_data (
  id                        integer not null,
  team_id                   integer,
  team_name                 varchar(255),
  year                      integer,
  league_id                 integer,
  input_id                  integer,
  value                     float,
  constraint pk_seasonal_data primary key (id))
;

create table team (
  id                        integer not null,
  name                      varchar(255),
  tm_id                     integer,
  logo                      varchar(255),
  league_id                 integer,
  constraint pk_team primary key (id))
;

create sequence fixed_data_seq;

create sequence input_seq;

create sequence league_seq;

create sequence seasonal_data_seq;

create sequence team_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists fixed_data;

drop table if exists input;

drop table if exists league;

drop table if exists seasonal_data;

drop table if exists team;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists fixed_data_seq;

drop sequence if exists input_seq;

drop sequence if exists league_seq;

drop sequence if exists seasonal_data_seq;

drop sequence if exists team_seq;

