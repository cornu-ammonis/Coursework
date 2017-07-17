import datetime 

def ex1(n):
	s = 0
	i = n
	while(i > 0):
		j = 1
		while(j < n):
			s = s + 1
			j = j * 2
		i = i / 2
	return s



def ex2(n):
	i = 1
	s = 0
	while(i < n):
		i = i *2
		j = n
		while(j > 0):
			k = j
			while(k < n):
				k = k + 2
				s = s + 1
			j = j/2
	return s



def time(N):
	before = datetime.datetime.now()
	ans = ex2(N)
	after = datetime.datetime.now()
	print ans
	return (after - before)


def doubling(N):
	prev = time(N)
	while(N < 20000000000000):
		N = N * 2
		t = time(N)
		s = "n : {0} time : {1} ratio: {2}\n".format(N, t, t.total_seconds()/prev.total_seconds())
		print s 
		prev = t

print(ex2(30))
		


