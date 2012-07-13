import Data.Array;
import Data.List;
import System.IO;

data Cell = Robot
          | Wall
          | Rock
          | Lambda
          | Lift
          | Dirt
          | Space
          deriving (Eq)

instance Show Cell where
  show Robot  = "R"
  show Wall   = "#"
  show Rock   = "*"
  show Lambda = "\\"
  show Lift   = "L"
  show Dirt   = "."
  show Space  = " "

type Map = Array (Int, Int) Cell

showMap m = unlines $ reverse $ buildRows w $ concat $ map show $ elems m
  where
    (w, h) = snd $ bounds m
    buildRows :: Int -> String -> [String]
    buildRows i x | length x > i = (take i x) : (buildRows i (drop i x))
                  | otherwise    = [x]

readCell c | c == 'R' = Robot
           | c == '#' = Wall
           | c == '*' = Rock
           | c == '\\' = Lambda
           | c == 'L' = Lift
           | c == '.' = Dirt
           | c == ' ' = Space

readMap :: String -> Map
readMap m = listArray ((1,1), (w,h)) $ map readCell (concat $ reverse l)
  where
    l = lines m
    h = length l
    w = length $ l !! 0

numLambdas m = length $ filter (== Lambda) $ elems m

findCell m obj = maybe (0,0) fst $ find (\(i, e) -> e == obj) $ assocs m

getFile = do
  h <- openFile "test.map" ReadMode
  m <- hGetContents h
  return $ readMap m