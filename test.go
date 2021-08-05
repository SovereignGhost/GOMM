func Integer: numPrint (Integer: num, Integer: length)
{
	Integer: i, j, first, temp;
	char : a;
	a := ‘x’;
	println(a);
	in>> i;
	println (i);
	temp := 1 + 2 + 3;
	println(i);
	while i > 0 :
	{
		first:= 99; /*this line contains a comment*/
		while j < i:
		{
			j := j + 1;
		}
		if j = 1:{
			print(j);
		}
		elif j = 2:{
			print(j);
		}
		else
		{
			if i = 1:{
				println(first);
			}
		}
	
		/* this is a comment */
		i:= i - 1;
		/*This is a
		Multiline
		Comment*/
	}
	println(temp);
}