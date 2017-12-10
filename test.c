int z;
int ee[10];
int t = 1;
void main ()
{
	int x; int j;
	int A[10];
	x = +726;
	j = -1;
	A[0] = 1;
	A[1] = 2;
	A[A[1]] = 3;
	write(A[A[1]]);
	write(A[1]);
	write(A[0]);
	abc(1,A);
	write(j);
	while(x > 100){
		if(x > 500){
			write(x);
			x = x / 2;
		}
		else {
			write(x);
			x = x / 2;
		}
	}
}

void abc(int a, int d[]){
	++a;
	write(a);
	++d[a];
	write(d[a]);
}