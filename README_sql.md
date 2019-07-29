## Vertex Querying

explain analyse        
select lv.id as l, rv.id as r
from lv, rv
where lv.dob = rv.dob and lv.company = rv.company;

## Edge Querying (Conjunctive)

create view vertexjoin as 
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


## Edge Querying (Disjunctive)

explain analyse
select src.*, dst.*
from le, re, vertexjoin as src, vertexjoin as dst
where (src.l = le.src and  dst.l = le.dst  and  src.r = re.src and dst.r = re.dst) 

UNION

select src.*, dst.*
from le, vertexjoin as src, vertexjoin as dst
WHERE  src.l = le.src and dst.l = le.dst and NOT EXISTS (select * from re where src.r = re.src and dst.r = re.dst)

UNION

select src.*, dst.*
from re, vertexjoin as src, vertexjoin as dst
WHERE  src.r = re.src and dst.r = re.dst and NOT EXISTS (select * from le where src.l = le.src and  dst.l = le.dst)
