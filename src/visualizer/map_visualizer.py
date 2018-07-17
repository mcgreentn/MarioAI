from definitions import ROOT_DIR
from PIL import Image

class MarioVisualizer:
    """
    Utility class that allows us to easily visualize mario levels without putting them through the game engine
    All it needs is the string representation of a level and the sprites to convert it!
    """

    def __init__(self):

        sheet_path = ROOT_DIR + "/img/mapsheet.png"
        # self.sprite_sheet = pg.image.load(sheet_path).convert()
        self.sprites = self.preprocess_mapsheet(sheet_path, (16,16), 16, 16)
        self.sprite_dict = {
            "X" : self.sprites[129],
            "S" : self.sprites[16],
            "-" : self.sprites[0],
            "?" : self.sprites[21],
            "Q" : self.sprites[4],
            #TODO Mike, fix E to some sort of viable placeholder for the enemy tile
            "E" : self.sprites[41],
            "<" : self.sprites[10],
            ">" : self.sprites[11],
            "[" : self.sprites[26],
            "]" : self.sprites[27],
            "o" : self.sprites[32],
            "B" : self.sprites[14],
            "b" : self.sprites[30]
        }

    def preprocess_mapsheet(self, sheet_path, size, rows, columns):
        """
        Strips individual frames from a sprite sheet given a start location,
        sprite size, and number of columns and rows.
        """
        sprite_sheet = Image.open(sheet_path)
        sprites = []
        for y in range(rows):
            for x in range(columns):
                a = (x + 1) * size[0]
                b = (y + 1) * size[1]
                sprite = sprite_sheet.crop((a - size[0], b - size[1], a, b))
                sprites.append(sprite)
        return sprites

    def cut_image(self, sprites, index):
        """
        Returns the image at the designated index
        :param index:
        :return: the sprite at the given index
        """
        return sprites[index]

    def visualize_map(self, char_map):
        """
        Visualizes the given map array
        :param map: the file we want to visualize. Must be a mario level map (or else it won't make a lot of sense..
        """
        # cross ref with dictionary, and put each image into its respective numpy position
        # stitch together all images of the array into one image
        # display this stitched image
        # sprite_array = [self.sprite_dict[x] for x in map]
        sprite_map = []
        for row in char_map:
            sprite_row = list(map(lambda x: self.sprite_dict[x], row))
            sprite_map.append(sprite_row)

        x = 0
        y = 0
        y_dim = len(char_map)
        x_dim = len(char_map[0])
        mega_image = Image.new("RGBA", (x_dim*16, y_dim*16))
        for row in sprite_map:
            for img in row:
                mega_image.paste(img, (x,y))
                x += 16
            x = 0
            y += 16
        mega_image.show()

    def load_map(self, map_path):
        """
        Reads in a mapfile and returns its array representation
        :param map_path: the path to the map file
        :return: the array rep of this map
        """
        with open(map_path, "r+") as map_file:
            read_map = map_file.read().splitlines()
            fixed_map = []
            for row in read_map:
                new_row = []
                for char in row:
                    new_row.append(char)
                fixed_map.append(new_row)
            return fixed_map

if __name__ == "__main__":
    visualizer = MarioVisualizer()
    level_2 = visualizer.load_map(ROOT_DIR + "/levels/mario-2-1.txt")

    visualizer.visualize_map(level_2)