#temp function to count lambdas on the field. 
def countLambdas(state):
    count = 0
    for row in state:
        for spot in row:
            if spot == '\\':
                count += 1

    return count

#pass in the number of ticks from the current point, and the current state
#since map y index is bottom to top, map(y+1) = array(y-1)
def mapAtTick(n, state):
    numLambdas = countLambdas(state) #what do I do with this?

    for i in range(n):
        x, y = 0,-1 #moving from left to right, bottom to top
        for row in reversed(state): #bottom to top
            for spot in row:
                if spot == 'L' and numLambdas == 0:
                    state[y][x] = 'O'

                if spot == '*':
                    print "found a rock " + str(x) + " " + str(y)
                    if y+1 < 0 and state[y+1][x] == ' ':
                        state[y][x] = ' '
                        state[y+1][x] = '*'
                    elif y+1 < 0 and x+1 < len(row) and state[y+1][x] == '*' and state[y][x+1] == ' ' and state[y+1][x+1] == ' ':
                        state[y][x] = ' '
                        state[y+1][x+1] = '*'
                    elif y+1 < 0 and x+1 < len(row) and x-1 > 0 \
                            and state[y+1][x] == '*' and (state[y][x+1] != ' ' or state[y+1][x+1] != ' ') and state[y][x-1] == ' ' and state[y+1][x-1] == ' ':
                        state[y][x] = ' '
                        state[y+1][x-1] = '*'
                    elif y+1 < 0 and x+1 < len(row) and state[y+1][x] == '\\' and state[y][x+1] == ' ' and state[y+1][x+1] == ' ':
                        state[y][x] = ' '
                        state[y+1][x+1] = '*'
                x += 1
            x = 0
            y -= 1
    return state
    
def displayState(state):
    for row in state:
        print row
            
map = [['#','#','#','#','#','#','#'],
       ['#','*','.','.','.','R','#'],
       ['#','*','.','.','.',' ','#'],
       ['#',' ','.','*',' ',' ','#'],
       ['#',' ','.',' ','.',' ','#'],
       ['#',' ','*','.','.',' ','#'],
       ['#',' ','*','.','.',' ','#'],
       ['#','.','.','.','.',' ','#'],
       ['#','#','#','#','#','L','#']]
