#temp state variables
waterLevel = 0
waterRate = 5
waterProof = 10
numLambdas = 0
lambdaPos = []
collectedLambdas = 0
robotPos = (-1,-1)
map = [['#','#' ,'#','#' ,'#','#','#'],
       ['#','*' ,' ','\\ ' ,'.','R','#'],
       ['#','*' ,' ',' ' ,'.',' ','#'],
       ['#','.' ,'.','*' ,' ',' ','#'],
       ['#','.' ,'.',' ' ,'.',' ','#'],
       ['#','.' ,'*','\\','.',' ','#'],
       ['#','\\','*','.' ,'.',' ','#'],
       ['#','.' ,'.','.' ,'.',' ','#'],
       ['#','#' ,'#','#' ,'#','L','#']]

#should only recieve L,R,U,D as a move
def moveRobot(state, move):
    global robotPos, collectedLambdas, numLambdas
    (x,y) = robotPos
    if move == 'L' and x-1 >= 0:
        xp, yp = x-1, y
    elif move == 'R' and x+1 < len(state[0]):
        xp, yp = x+1, y
    elif move == 'U' and y+1 < len(state):
        xp, yp = x, y+1
    elif move == 'D' and y - 1 >= 0:
        xp, yp = x, y-1
    else:
        print "not a move " + move
        return

    if state[yp][xp] == 'O':
        print "you won!"
    
    if state[yp][xp] == '*' or state[yp][xp] == '#' or state[yp][xp] == 'L':
        return
    if state[yp][xp] == '\\':
        collectedLambdas += 1
        numLambdas -= 1
    state[y][x] = ' '
    state [yp][xp] = 'R'
    robotPos = (xp,yp)
    

#pass in the number of ticks from the current point, and the current state
def tickMap(n, state):
    for i in range(n):
        x, y = 0,0 #moving from left to right, bottom to top
        for row in state: #bottom to top
            for spot in row:
                if spot == 'L' and numLambdas == 0:
                    state[y][x] = 'O'

                if spot == '*':
                    if y-1 > 0 and state[y-1][x] == ' ':
                        state[y][x] = ' '
                        state[y-1][x] = '*'
                    elif y-1 > 0 and x+1 < len(row)-1 and state[y-1][x] == '*' and state[y][x+1] == ' ' and state[y-1][x+1] == ' ':
                        state[y][x] = ' '
                        state[y-1][x+1] = '*'
                    elif y-1 > 0 and x+1 < len(row)-1 and x-1 > 0 \
                            and state[y-1][x] == '*' and (state[y][x+1] != ' ' or state[y-1][x+1] != ' ') and state[y][x-1] == ' ' and state[y-1][x-1] == ' ':
                        state[y][x] = ' '
                        state[y-1][x-1] = '*'
                    elif y-1 > 0 and x+1 < len(row)-1 and state[y-1][x] == '\\' and state[y][x+1] == ' ' and state[y-1][x+1] == ' ':
                        state[y][x] = ' '
                        state[y-1][x+1] = '*'
                x += 1
            x = 0
            y += 1
#    return state
    
def displayState(state):
    global waterLevel
    print "WaterLevel: " + str(waterLevel)
    for row in reversed(state):
        print row
            

def findLambdas(state):
    x, y = 0,0
    result = []
    for row in state:
        for spot in row:
            if spot == '\\':
               result.append((x,y))
            x += 1
        x = 0
        y += 1
    return result

def findRobot(state):
    x, y = 0,0
    for row in state:
        for spot in row:
            if spot == 'R':
                return (x,y)
            x += 1
        x = 0
        y += 1
    return (-1,-1)
    
robotPos = findRobot(map)
lambdaPos = findLambdas(map)
numLambdas = len(lambdaPos)

print robotPos
print numLambdas

i = 0
displayState(map)
print "====================================="
while True:
    if waterRate != 0 and i%waterRate == waterRate-1:
        waterLevel += 1
    moveRobot(map, raw_input("Enter a move: "))
    tickMap(1,map)
    displayState(map)
    i += 1
    print "====================================="
