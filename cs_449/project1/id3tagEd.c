/*
* Zachary Whitney  zdw9  ID: 3320178
* CS 449 Summer 2018  Prof: Johnathan Misurda
* Recitation: Tues 9:30 AM
* Assignment #1, Part B
* This is simple utility which can print, add, and adjust ID3 v1.1 metadata
* in multimedia files from the command line
*/

#include <stdio.h>
#include <string.h>

#define FALSE 0
#define TRUE 1

#define TAG_LENGTH 128
#define IDENTIFIER_LENGTH 3
#define TITLE_LENGTH 30
#define ARTIST_LENGTH 30
#define ALBUM_LENGTH 30
#define YEAR_LENGTH 4
#define COMMENT_LENGTH 28

#define TITLE_OFFSET 3
#define ARTIST_OFFSET 33
#define ALBUM_OFFSET 63
#define YEAR_OFFSET 93
#define COMMENT_OFFSET 97
#define ZB_OFFSET 125
#define TRACK_OFFSET 126
#define GENRE_OFFSET 127

struct id3_tag {
    char identifier[IDENTIFIER_LENGTH];
    char title[TITLE_LENGTH];
    char artist[ARTIST_LENGTH];
    char album[ALBUM_LENGTH];
    char year[YEAR_LENGTH];
    char comment[COMMENT_LENGTH];
    char zero_byte;
    char track;
    char genre;
};

//prototypes
void update_field(char field[], char value[], struct id3_tag *ptr);
void print_tag(struct id3_tag *ptr);
void overwrite_tag(FILE *fp, struct id3_tag *id3tag);
void append_tag(FILE *fp, struct id3_tag *id3tag);

int main(int argc, char* argv[])
{
    int hasTag = FALSE;             //boolean -- does this file have an id3 tag?
    struct id3_tag tag;
    memset(&tag, 0, TAG_LENGTH);    //zero out the struct fields

    if(argc < 2)
    {
        printf("Usage: ./id3tagEd filename\n\n");
        printf("optional additional arguments: -field_to_change new_value\n");
        printf("fields and values must be specified in pairs, in that order\n");
        return 1;
    }
    FILE *file = fopen(argv[1], "r+b"); //read and write in binary format
    if (file == NULL)
    {
        printf("%s is not a valid filepath\n", argv[1]);
        return 2;
    }

    //seek to tag start in the file, then read into tag if seek is successful
    if(fseek(file, -TAG_LENGTH, SEEK_END) == 0)
        fread(&tag, sizeof(char), TAG_LENGTH, file);

    if (strncmp(tag.identifier, "TAG", 3) == 0)
        hasTag = TRUE;           

    if(argc == 2) //just check for tag and print if it exists
    {
        if(!hasTag)
        {
            printf("%s does not have an id3 tag.\n", argv[1]);
            fclose(file);
            return 0;
        }
        printf("Here is the ID3 tag for %s:\n\n", argv[1]);
        print_tag(&tag);
        fclose(file);
        return 0;
    }

    if(!hasTag)
    {
        memset(&tag, 0, TAG_LENGTH);  //clear junk from fread
        printf("%s does not have an id3 tag.\n\n", argv[1]);
    }

    //consume extra command line arguments and make modifications in the struct
    int change_tracker = 2;
    char field[30];     //to hold the field to change
    char value[30];     //to hold the value to store in the field
    while(change_tracker < argc)
    {
        if(change_tracker % 2 == 0) //even argument #s specify fields
            strcpy(field, argv[change_tracker++]);
        else{
            strcpy(value, argv[change_tracker++]);
            update_field(field, value, &tag);
        }
    }

    //write data to file and display results to the user
    if(hasTag)
    {
        overwrite_tag(file, &tag);
        printf("Here is the adjusted ID3 tag for %s:\n\n", argv[1]);
    }
    else
    {
        append_tag(file, &tag);
        printf("Here is the newly created ID3 tag for %s:\n\n", argv[1]);
    }
    print_tag(&tag);

    fclose(file);
    return 0;
}

//changes the specified field in the struct to the new value
//accepts "-field" or "field" forms, but only in all lowercase exact matches
void update_field(char field[], char value[], struct id3_tag *ptr)
{
    if(strcmp(field, "-title") == 0 || strcmp(field, "title") == 0)
        strncpy(ptr->title, value, TITLE_LENGTH);
    if(strcmp(field, "-artist") == 0 || strcmp(field, "artist") == 0)
        strncpy(ptr->artist, value, ARTIST_LENGTH);
    if(strcmp(field, "-album") == 0 || strcmp(field, "album") == 0)
        strncpy(ptr->album, value, ALBUM_LENGTH);
    if(strcmp(field, "-year") == 0 || strcmp(field, "year") == 0)
        strncpy(ptr->year, value, YEAR_LENGTH);
    if(strcmp(field, "-comment") == 0 || strcmp(field, "comment") == 0)
        strncpy(ptr->comment, value, COMMENT_LENGTH);
    if(strcmp(field, "-track") == 0 || strcmp(field, "track") == 0)
        ptr->track = value[0]; //this is a 1-char value
}

//overwrites an existing id3_tag in the file with the contents of the program's id3tag
void overwrite_tag(FILE *fp, struct id3_tag *id3tag)
{
    fseek(fp, -TAG_LENGTH, SEEK_END);
    fwrite(id3tag, sizeof(char), TAG_LENGTH, fp);
}

//adds data[] to the end of the file
void append_tag(FILE *fp, struct id3_tag *id3tag)
{
    strncpy(id3tag->identifier, "TAG", TITLE_OFFSET);  //put ID3 TAG identifier in tag
    fseek(fp, 0, SEEK_END);
    fwrite(id3tag, sizeof(char), TAG_LENGTH, fp);
}

//prints out an id3_tag's fields to the console by using a format specifier as a string terminator
//skips tag identifier and zero_byte fields
void print_tag(struct id3_tag *ptr)
{
    struct id3_tag t = *ptr;
    printf("Title: %.30s\n", t.title);
    printf("Artist: %.30s\n", t.artist);
    printf("Album: %.30s\n", t.album);
    printf("Year: %.4s\n", t.year);
    printf("Comment: %.28s\n", t.comment);
    printf("Track: %c\n", t.track);
    printf("Genre: %c\n\n", t.genre);
}
