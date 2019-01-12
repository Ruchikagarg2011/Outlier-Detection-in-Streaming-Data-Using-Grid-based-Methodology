# Outlier-Detection-in-Streaming-Data-Using-Grid-based-Methodology
Detected outliers with sliding window on data streams by computing/updating belongingness of each point calculated using the sparsity of their corresponding region

Input: A	window	size	w (32- bit	integer),	a	: pair	where	you	receive	an	input	stream	of	fixed-ddimensional integer	data	points	in a	comma	separated	values	(CSV)	format with	timestamp	added	in	front.

Output: Outliers	starting	from	the	(w+1)th input	data	until	end	of	input	stream.	Your	program	should	handle	concept	drift	too.

The	idea	is	to	determine	sparse	regions	in	the	underlying	data	in	order	to	report	outliers. In	the	context	of	multivariate data,	a natural	generalization	is	the	use	of	grid-structure.	Each	dimension	is	partitioned into	p equal-width	ranges.	Data	points that have	density	less	than	t	in	any	particular	grid	region	are	reported as	outliers. To	handle	streaming	data	with	sliding	window,	we extend	density to	incremental	scenarios:	1)	the	statistic	of	the	newly	inserted data	point is computed,	2) only	the	density of	the affected	data	points	by	the	newly	inserted	data	point	in	the	existing	data	points	in	the	window	are	updated	and	3)	similarly updated	the	deleted	data points.

