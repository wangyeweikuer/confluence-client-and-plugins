SELECT SPACE0_.SPACEKEY AS X0_0_ 
FROM   SPACES SPACE0_ 
WHERE  ( SPACE0_.SPACEID NOT IN(SELECT DISTINCT SPACE2_.SPACEID 
                                FROM   SPACEPERMISSIONS SPACEPERMI1_, 
                                       SPACES SPACE2_ 
                                WHERE  SPACEPERMI1_.SPACEID = SPACE2_.SPACEID 
                                       AND ( ( ( 
                SPACEPERMI1_.PERMUSERNAME = '2C9681F6419CE60D0141B62C9DFB0009' 
                        ) 
                         OR ( SPACEPERMI1_.PERMGROUPNAME IN( 
                                    'CONFLUENCE-USERS', 
                                    'TESTGROUP1_10', 
                                    'TESTGROUP1_100', 
                                    'TESTGROUP1_20', 
                                    'TESTGROUP1_50' ) ) 
                         OR ( ( SPACEPERMI1_.PERMUSERNAME IS NULL 
                              ) 
                              AND ( SPACEPERMI1_.PERMGROUPNAME IS 
                                    NULL ) ) ) 
                      AND (( SPACEPERMI1_.PERMTYPE = 'VIEWSPACE' ) 
                          ) )) )
limit 10;
