DROP TABLE IF EXISTS TRAIN_ROUTE CASCADE;

CREATE TABLE TRAIN_ROUTE (
    description		VARCHAR(200),
    route_ID        SERIAL,
    
    CONSTRAINT r_PK PRIMARY KEY(route_id)
);
