var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var mongoose = require('mongoose');
var passport = require('passport');
var config = require('./config/database');
var morgan = require('morgan');
var app = express();

// Routes for the app
var index = require('./routes/index');
var user = require('./routes/user');
var fetch = require('./routes/fetch');
var setting = require('./routes/setting');
var post = require('./routes/post')
var comment = require('./routes/comment')
var fileUpload = require('express-fileupload');
mongoose.connect(config.database);

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

// uncomment after placing your favicon in /public
//app.use(favicon(path.join(__dirname, 'public', 'favicon.ico')));
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));
app.use(function(req, res, next) {
  res.header("Access-Control-Allow-Origin", "*");
  res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
  next();
});
app.use(fileUpload());
app.use(passport.initialize());
app.use('/', index);
app.use('/user', passport.authenticate('jwt', {session: false}), user);
app.use('/setting', passport.authenticate('jwt', {session: false}), setting);
app.use('/fetch', passport.authenticate('jwt', {session: false}), fetch);
app.use('/post',passport.authenticate('jwt',{session:false}), post);
app.use('/post',passport.authenticate('jwt',{session:false}),comment);
// catch 404 and forward to error handler
app.use(function(req, res, next) {
  var err = new Error('Not Found');
  err.status = 404;
  next(err);
});

// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};
  // render the error page
//  res.status(err.status || 500);
    res.send({success:false,msg:"Shit some error"})
    console.log(err);
});

module.exports = app;
