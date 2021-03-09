from geoRequest import retrieveGeoInfo, retrieveSimilarAnswers 
from flask import Flask
from flask_restful import Api,Resource,reqparse   #for parse request and replies automatically    
            

app = Flask(__name__)
api = Api(app) 


'''import time
start_time = time.time()


lat, long, country,locality,city= retrieveGeoInfo()

print("lat: "+lat, "long: "+long,"country: "+country, "locality: "+ locality, "city: "+city )

print("----------------------------------")
print(retrieveSimilarAnswers( country,locality))

print("--- %s seconds ---" % (time.time() - start_time))'''

#kinds of get requests:
INIT_STATE = 0 
    #GET0: req=0 [required] + game_id="1234567" [not required (IDENTIFY THE GAME)](sended by FIRST player)
        #return id player for the game
              #+WAITING=true
              #+ set STATE = SYNC_STATE
    
    #GET0: req=0 [required] + game_id="1234567" [ (IDENTIFY THE GAME started by player 2)
         #return id player for the game 
         #+set WAITING =false
         # + set STATE = PLAY_STATE
SYNC_STATE = 1 
    #GET1: req=1 [required] + game_id="1234567" //polling on variable WAITING
        #return |||WAITING (=true) (if waiting other player ) or WAITING( =false) (player2 is ready)
        #IF WAITING== false => set STATE = PLAY_STATE
  
  
PLAY_STATE = 2
    #GET2: req=2 [required] + game_id="1234567" [ required] + player_id //PLAYER 1 WANT START TO PLAY 
        #set wait_next_lev =false 
        #return: geo coordinates and names
        # + set STATE = LEVEL_STATE
    
    #polling:
    #GET2: req=2 [required] + game_id="1234567" [ required] + player_id //PLAYER 2 do polling  
        #polling on wait_next_lev variable
        #return:  wait_next_lev variable 
                # (if false => +geo coordinates and names)       
LEVEL_STATE = 3
    #GET3: req=3 [required] + game_id="1234567" //NOTIFY THAT A PLAYER HAS COMPLETED THE LEVEL
        #SET |||WAITING (=FALSE) (if is the second req=3 with the same game_id ) 
        #      or WAITING( =true) (if is the first req=3 with the same game_id ) 

WAITING_STATE = 4
    #GET4: req=4 [required] + game_id="1234567" //polling on variable WAITING
        #return |||WAITING (=true) (if waiting other player ) or WAITING( =false) (player2 is ready)
        #IF WAITING== false => set STATE = PLAY_STATE
ID_PLAYER = -1




STATE = INIT_STATE

list_game_id =[]
coordinates_game = {}
first_req = {}
sync={}
waiting = {}
random_dict ={}
parser = reqparse.RequestParser()

class GeoApp(Resource):    #Resource for use Crud op and other...
   
    def get(self):
        parser.add_argument('req',type=int,required = True)
        parser.add_argument('game_id',type=int,required = False)
        parser.add_argument('player_id',type=int,required = False)
        parser.add_argument('random',type=int,required = False)#=0=> False|| =1=>True
        parser.add_argument('level',type=int,required = False)#number of current level s.t. can be decided the correspondent difficulty
        args = parser.parse_args() #parse the msg

        req = args['req'] #0||1||2||3||4
        game_id = args['game_id'] #0||1||2||3||4......
        player_id = args['player_id']
        random = args['random']
        level = args['level']
        
        if(req == INIT_STATE):
            if(game_id == None):
                if(random == None or random==0): #not random game
                    g_id = len(list_game_id)
                    list_game_id.append(g_id)
                    random_dict[g_id] = False
                    sync[g_id]=True
                    first_req[g_id] = True
                    return {"error":False, 'msg':"return game_id and id_player", 'game_id':g_id,'player_id':0}
                elif(random!= None and random==1): #random game                   
                    found=False
                    for key in random_dict:
                        if random_dict[key]:
                            found = True
                            g_id = key
                            sync[g_id]=False
                            random_dict[g_id] = False
                            return {"error":False, 'msg':"return game_id and id_player", 'game_id':g_id,'player_id':1}
                        
                    if(not found):   
                        g_id = len(list_game_id)
                        list_game_id.append(g_id)
                        random_dict[g_id] = True
                        sync[g_id]=True
                        first_req[g_id] = True
                        return {"error":False, 'msg':"return game_id and id_player", 'game_id':g_id,'player_id':0}
                
            else: #exist game_id means that no random game
                if(game_id in sync):
                    sync[game_id]=False
                    return {"error":False, 'msg':"return game_id and id_player", 'game_id':game_id,'player_id':1}
                else:
                    return {"error":True, 'msg':"Error: game_id ="+str(game_id)+" not exist"}
        
        elif(req == SYNC_STATE):
            if(not sync[game_id]): STATE = PLAY_STATE
            return {"error":False, 'msg':"return sync_state", 'waiting':sync[game_id]}
        
        elif(req==PLAY_STATE):
                if( (game_id in list_game_id) and player_id==0 and first_req[game_id]):
                    first_req[game_id] = False
                    lat, long, country,city = retrieveGeoInfo(level,3) 
                    f_lat,f_long,false_country,false_city = retrieveGeoInfo(level,1) 
                    false_answers = retrieveSimilarAnswers(country,city,false_country,false_city)
                    
                    coordinates_game[game_id] = [lat,long,country,city,false_answers]
                    STATE = LEVEL_STATE
                    return {'error':False, 'msg':"return true answer and the three false answers", 
                                'Country' :country[0],'City':city[0], 'lat' :lat,'long':long,
                                'fCountry1':false_answers[0]['country'],'fCity1':false_answers[0]['city'],
                                'fCountry2':false_answers[1]['country'],'fCity2':false_answers[1]['city'],
                                'fCountry3':false_answers[2]['country'],'fCity3':false_answers[2]['city']}

                elif((game_id in list_game_id) and player_id==1 and not first_req[game_id]):
                    first_req[game_id] = True
                    lat,long,country,city,false_answers=coordinates_game[game_id]
                    #coordinates_game[game_id]=None
                    return {'error':False, 'msg':"return true answer and the three false answers", 
                                'Country' :country[0],'City':city[0], 'lat' :lat,'long':long,
                                'fCountry1':false_answers[0]['country'],'fCity1':false_answers[0]['city'],
                                'fCountry2':false_answers[1]['country'],'fCity2':false_answers[1]['city'],
                                'fCountry3':false_answers[2]['country'],'fCity3':false_answers[2]['city']}

                elif((game_id in list_game_id) and player_id==1 and first_req[game_id]):
                    return {'error':True, 'msg':"wait the other player"}
                    
        elif(req == LEVEL_STATE):
            if(game_id not in waiting): waiting[game_id] = True
            else: waiting[game_id] = False
            return {"error":False, 'msg':"level completed", 'game_id':game_id}
        
        elif(req == WAITING_STATE):
            if(not waiting[game_id]): STATE = PLAY_STATE
            return {"error":False, 'msg':"return waiting_state", 'waiting':waiting[game_id]}
        
'''
    def post(self):
        parser.add_argument('req',type=int,required = True) 
        parser.add_argument('who',type=int,required = True)
        parser.add_argument('moveX',type=int,required = True)
        parser.add_argument('moveY',type=int,required = True)
        args = parser.parse_args() #parse the msg

        req = args['req']
        who = args['who']
        moveY = args['moveY']
        moveX = args['moveX']

        parser.remove_argument('moveX')
        parser.remove_argument('moveY')

        if req == MOVE:
            lastMove[who] = [moveX,moveY]
            return {"error":False, 'msg':"MOVE MESSAGE", 'moveX':lastMove[who][0], 'moveY': lastMove[who][1]}
            
        else:
            return{"error": True, 'msg':"'req' !=2}"}


'''

api.add_resource(GeoApp,"/")

if __name__ == "__main__":
    print("starting api...")
    #app.run()
    #app.run(host = "0.0.0.0")
    from waitress import serve
    serve(app, host="0.0.0.0", port=8080)

