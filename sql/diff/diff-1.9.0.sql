DROP PROCEDURE CheckAdminRights

-- 2004-04-27 Kreiger

DROP PROCEDURE GetChilds

-- 2004-05-04 Kreiger

UPDATE users SET login_name=RTRIM(login_name), login_password=RTRIM(login_password),
    first_name=RTRIM(first_name), last_name=RTRIM(last_name), title=RTRIM(title),
    company=RTRIM(company), address=RTRIM(address), city=RTRIM(city),  zip=RTRIM(zip),
    country=RTRIM(country),  county_council=RTRIM(county_council),  email=RTRIM(email)

-- 2004-05-11  Lennart
