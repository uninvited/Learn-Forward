# coding: utf-8
import argparse
import json
import xlrd

parser = argparse.ArgumentParser()
parser.add_argument('--input', help='Input .xlsx file path', required=True)
parser.add_argument('--output', help='Ouput file path', required=True)
parser.add_argument('--unit_offset', help='Word ID offset for the unit', required=True)

args = parser.parse_args()

IDX_WORD_EN = 0
IDX_TRANSC = 1
IDX_EXAMPLE = 2
IDX_CLASS = 3
IDX_WORD_RU = 4
IDX_UNIT_ID = 5
IDX_TOPICS = 6
IDX_SIBLINGS = 7
IDX_EXAMPLE_ANSWER = 8 

rb = xlrd.open_workbook(args.input)
sheet = rb.sheet_by_index(0)

words = []
counter = int(args.unit_offset)

with open(args.output, "w") as file:
    for rownum in range(sheet.nrows):
        row = sheet.row_values(rownum)

        word_en = row[IDX_WORD_EN]
        example = row[IDX_EXAMPLE]
        word_class = row[IDX_CLASS].encode('utf-8')
        transcription = row[IDX_TRANSC].encode('utf-8')
        word_ru = row[IDX_WORD_RU].encode('utf-8')
        unit_id = int(row[IDX_UNIT_ID])
        topics = row[IDX_TOPICS]
        siblings = row[IDX_SIBLINGS]
        example_answer = row[IDX_EXAMPLE_ANSWER]

        print 'Handle word', word_en

        topic_ids = []
	if not topics:
            pass
        elif isinstance(topics, float) or isinstance(topics, int):
            topic_ids.append(int(topics))
        else:
            for id in topics.split(','):
                topic_ids.append(int(id))
        print topic_ids

        item = {}
        item['id'] = counter
        item['name_en'] = word_en
        item['tscp'] = transcription
        item['example'] = example
        item['class'] = word_class
        item['name_ru'] = word_ru
        item['unit_id'] = unit_id
        item['topics'] = topic_ids
        item['siblings'] = siblings
        item['example_answer'] = example_answer
        words.append(item)
        counter += 1
    file.write(json.dumps(words))
