default: test-hwAPI

hardwareAPI.o: hardwareAPI.c
	gcc -c $^

test-hwAPI: test-hwAPI.c hardwareAPI.o
	gcc -o $@ $^ -lpthread -lnsl
