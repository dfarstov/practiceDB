SELECT 
	r.name,
	c.name,
	p.oil,
	p.cheese
FROM 
	practice.products_statistics p
JOIN 
	countries c on c.id_country = p.id_country
JOIN 
	regions r on r.id_region = c.id_region;