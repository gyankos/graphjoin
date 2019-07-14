## Vertex Querying

explain analyse        
select lv.id as l, rv.id as r
from lv, rv
where lv.dob = rv.dob and lv.company = rv.company;

## Edge Querying

create view vertices as 
select lv.id as l, rv.id as r
from lv, rv
where lv.dob = rv.dob and lv.company = rv.company;

explain analyse
select src.l, src.r, dst.l, dst.r
from (select lv.id as l, rv.id as r
from lv, rv
where lv.dob = rv.dob and lv.company = rv.company) src,
     (select lv.id as l, rv.id as r
from lv, rv
where lv.dob = rv.dob and lv.company = rv.company) dst,
     le, re
where src.l = le.src and 
      dst.l = le.dst and
      src.r = re.src and
      dst.r = re.dst;
