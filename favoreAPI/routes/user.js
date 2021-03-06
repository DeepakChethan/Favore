var mongoose = require('mongoose');
var passport = require('passport');
var config = require('../config/database');
require('../config/passport')(passport);
var express = require('express');
var jwt = require('jsonwebtoken');
var router = express.Router();
var User = require("../models/user");
var Post = require('../models/post');


router.post('/updateProfile',(req,res,next)=>{
  var path = "public/images/"+req.body.id+".jpg";
  var stat = uploadImage(req,res,path);
  if(stat==false){
    res.json({success:false,msg:"Timeout mate!",error:err});
    return;
  }else{
    res.json({success:true,msg:"Updated your profile pic!"});
  }
});

router.post('/getuserposts/:usr_id',function(req,res,next){
    var uid = req.params.usr_id;
    Post.find({posterId:uid},function(err,posts){
	if (err){
	    res.json({success:false,msg:"unable to fetch posts"});
	}else{
	    res.json({success:true,post:posts});
	}
});
});



function uploadImage(req,res,path){
  // Images available at imagepath/postId.jpg
  var img = req.files.file;
  if (!req.files){
    res.json({success:false,msg:"No image to upload bch!"});
    return false;
  }
  else {
  img.mv("public"+path,function(err){
    if (err){
      res.json({success:false,msg:"Failed to upload the file",error:err});
      return false;
    }
    else return true;
  });
}
}




router.post("/editUser/",function(req,res,next){
  var user_details = req.body;
  var path = "public/images/"+user_details.id+".jpg";
  var stat = uploadImage(req,res,path);
  if (stat == false){
    res.json({success:false,msg:"Time out!"});
    return;
  }
  var path="/images/"+user_details.id+".jpg";
  User.update({id:user_details.id},{$set:{
    fname:user_details.fname,
    lname:user_details.lname,
    dname:user_details.dname,
    phone:user_details.phone,
    age:user_details.age,
    bio:user_details.bio,
    profileImg:path
  }},function(err,out){
    if (err){
      res.json({success:false,msg:"Some kind of error"});
    }
    else{
      res.json({success:true,msg:out})
    }
  })
});

router.get('/getUserDetails/:user_id',function(req,res,next) {
  /*
  usage: localhost:3000/<usr-name>
  */
  var user_id = req.params.user_id;
  User.find({id:user_id},function(err,usr){
    if (err){
      req.json({success:false,message: "Some kind of error!"});
    }
    if (usr == null){
        res.json({success:false,msg:"No such user"});
    }
    else res.json({success:true,user:usr});
  });
})
// For testing
router.get('/getAllUsers',function(req,res,next){
  /*
    usage:localhost:3000/
    Returns all the uses in the collection
  */
  User.find(function(err,usrs){
    if (err) throw err;
    else res.json({"usr":usrs});
  })
});



router.get("/addFriend/",function(req,res,next){
  /*
    usage: localhost:3000/user/addFriend?usr=xx&frnd=xx
  */
  var user_id = req.query.usr;
  var frnd_id = req.query.frnd;
  if (user_id == frnd_id){
    res.json({success:false,msg:"Same user dumbo"});
  }
  else{
  // Check if the frind exists
  User.findOne({id:frnd_id},function(err,user){
    if (err){
      res.json({success:false,msg:"Some kind of the error!"});
    }
    else if (user==null){
      res.json({success:false,msg:"No such frind"})
    }
    else{
      User.findOne({id:user_id},function(err,ur){
        if (err){
          res.json({success:false,msg:"Some kind of the error!"});
        }else if (ur.friends.indexOf(frnd_id) != -1) {
          res.json({success:false,msg:"Already a friend"});
        }else{
          var friends_list = ur.friends;
          friends_list.push(frnd_id);
          User.update({id:user_id},{$set:{friends:friends_list}},function(err,out){
            if (err){
              res.json({success:false,msg:"Some kind of error"});
            }
            else res.json({success:true,msg:out});
          });
        }
      });
    }
  });
}
});

router.get("/removeFriend/",function(req,res,next){
  var user_id = req.query.usr;
  var frnd_id = req.query.frnd;
  if (user_id == frnd_id){
    res.json({success:false,msg:"Same user dumbo"});
  }
  else{
  // Check if the frind exists
  User.findOne({id:frnd_id},function(err,user){
    if (err){
      res.json({success:false,msg:"Some kind of the error!"});
    }
    else if (user==null){
      res.json({success:false,msg:"No such friend"})
    }
    else{
      User.findOne({id:user_id},function(err,ur){
        if (err){
          res.json({success:false,msg:"Some kind of the error!"});
        }else if (ur.friends.indexOf(frnd_id) == -1) {
          res.json({success:false,msg:"Not a friend"});
        }else{
          var friends_list = ur.friends;

          friends_list.splice(friends_list.indexOf(frnd_id,1));

          User.update({id:user_id},{$set:{friends:friends_list}},function(err,out){
            if (err){
              res.json({success:false,msg:"Some kind of error"});
            }
            else res.json({success:true,msg:out});
          });
        }
      });
    }
  });
}
});

router.get("/getFriendList/:user_id",function(req,res,next){
  User.findOne({id:req.params.user_id},function(err,user){
    if(err){
      res.json({success:false,msg:"some kind of error"});
    }else{
      res.json({success:true,friends:user.friends});
    }
  });
});


module.exports = router;
