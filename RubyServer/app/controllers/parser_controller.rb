class ParserController < ApplicationController
  require 'net/http'
  require "uri"
  
  def index
  end

  def times
    rte_number = params[:route]
    rte_dir = params[:direction]
    rte_stop = params[:stop]
    
    uri = URI.parse("http://webwatch.itsmarta.com/UpdateWebMap.aspx?u=" + rte_number)
    urlResponse = ""
    urlResponse = Net::HTTP.get_response(uri)
    
    routeInfo = []
    
    indexOfFirstStar = urlResponse.body.index("*")
    @response = Response.new
    @response.status = "GOOD"
    @response.currentTime = urlResponse.body[0...indexOfFirstStar]
    unparsedRouteInfo = urlResponse.body[indexOfFirstStar+1..-1]
    unparsedRouteInfo.split("*").each do |star|
      if( ! /.*Next Timepoint:.*/.match(star) )
        stopStuff = star.split(/\|[0-9]*;/)
        stopStuff.each do |route|
          info = route.split("||")
          stopInfo = info[0].split("|")
          times = info[1]
          lat = stopInfo[0].to_f
          lon = stopInfo[1].to_f
          stop = stopInfo[2]
          direction = stopInfo[3]
          ri = RouteInfo.new(stop, direction, lat, lon, rte_number.to_i)
          if /<br>/.match(times) 
            times.split("<br>").each do |t|
              ri.addTime(t)
            end
          end
          
          routeInfo << ri
        end
      end
    end
    
    data = RouteInfo.find_by_stop_and_direction(rte_stop, rte_dir)
    if data != nil
      @response.data = data
      @response.status = "GOOD"
    else
      @response.data = {Error: {message: "Stop not found"}}
      @response.status = "FAIL"
    end
    
    
    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @response }
    end
  end
  
  def bus
    rte_number = params[:route]
    
    #uri = URI.parse("http://koenigkreations.com/docs/marta_sample.txt")
    uri = URI.parse("http://webwatch.itsmarta.com/UpdateWebMap.aspx?u=" + rte_number)
    urlResponse = ""
    urlResponse = Net::HTTP.get_response(uri)
    
    busInfo = []
    
    indexOfFirstStar = urlResponse.body.index("*")
    @response = Response.new
    @response.status = "GOOD"
    @response.currentTime = urlResponse.body[0...indexOfFirstStar]
    unparsedRouteInfo = urlResponse.body[indexOfFirstStar+1..-1]
    unparsedRouteInfo.split("*").each do |star|
      if(  /.*Next Timepoint:.*/.match(star) )
          star = star.gsub('&amp;','##')
          buses = star.split(/;/)
          
          buses.each do |bus|
            bus = bus.gsub('##', '&amp;')
            allData = bus.split("|")
            bi = Bus.new
            bi.lat = allData[0]
            bi.lon = allData[1]
            
            busStuff = allData[3].split("<br>")
            
            startOfBnum = busStuff[1].index(":") + 2
            startOfStop = busStuff[2].index(":") + 2
            bi.num = busStuff[1][startOfBnum..-1].to_i
            bi.next_stop = busStuff[2][startOfStop..-1]
            
            bi.route = rte_number.to_i
            if allData[2].to_i == 3
              bi.direction = 'Eastbound'
            elsif allData[2].to_i == 4
              bi.direction = 'Southbound'
            elsif allData[2].to_i == 5
              bi.direction = 'Westbound'
            elsif allData[2].to_i == 8
              bi.direction = 'Northbound'
            else
              bi.direction = 'unknown'
            end
            
            busInfo << bi
          end
      end
    end
    
    data = busInfo
    if data != nil
      @response.data = data
      @response.status = "GOOD"
    else
      @response.data = {Error: {message: "No buses currently on route " + rte_number}}
      @response.status = "FAIL"
    end
    
    
    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @response }
    end
  end
  
  def stops
    @response = Response.new
    @response.status = "GOOD"
    urlResponse = ""
    routeInfo = []
    routes = [1,2,3,4,5,6,8,9,12,13,15,16,19,21,24,25,26,27,30,32,33,34,36,37,39,42,47,49,50,51,53,55,56,58,60,66,67,68,71,73,74,75,78,81,82,83,84,85,86,87,79,93,95,99,103,104,107,110,111,114,115,116,117,119,120,121,123,124,125,126,132,140,143,148,150,153,155,162,165,170,172,178,180,181,183,185,186,189,193,520,521]
  
    routes.each do |u|
      uri = URI.parse("http://webwatch.itsmarta.com/UpdateWebMap.aspx?u=" + u.to_s)
      urlResponse = Net::HTTP.get_response(uri)
      
      indexOfFirstStar = urlResponse.body.index("*")
      if indexOfFirstStar != nil
        
        @response.currentTime = urlResponse.body[0...indexOfFirstStar]
        
        unparsedRouteInfo = urlResponse.body[indexOfFirstStar+1..-1]
        unparsedRouteInfo.split("*").each do |star|
          if( ! /.*Next Timepoint:.*/.match(star) )
            stopStuff = star.split(/\|[0-9]*;/).each do |route|
              info = route.split("||")
              stopInfo = info[0].split("|")
              times = info[1]
              lat = stopInfo[0].to_f
              lon = stopInfo[1].to_f
              stop = stopInfo[2]
              direction = stopInfo[3]
              ri = RouteInfo.new(stop, direction, lat, lon, u)
              if /<br>/.match(times)
                times.split("<br>").each do |t|
                  ri.addTime(t)
                end
              end
              
              routeInfo << ri
            end
          end
        end
      end
    end
    
    data = routeInfo
    if data != nil
      @response.data = data
      @response.status = "GOOD"
    else
      @response.data = {Error: {message: "Stop not found"}}
      @response.status = "FAIL"
    end
    
    
    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @response }
    end
  end
  
  def stops_by_route
    @response = Response.new
    @response.status = "GOOD"
    rte_number = params[:route]
    urlResponse = ""
    routeInfo = []
  
    uri = URI.parse("http://webwatch.itsmarta.com/UpdateWebMap.aspx?u=" + rte_number)
    urlResponse = Net::HTTP.get_response(uri)
    
    indexOfFirstStar = urlResponse.body.index("*")
    if indexOfFirstStar != nil
      
      @response.currentTime = urlResponse.body[0...indexOfFirstStar]
      
      unparsedRouteInfo = urlResponse.body[indexOfFirstStar+1..-1]
      unparsedRouteInfo.split("*").each do |star|
        if( ! /.*Next Timepoint:.*/.match(star) )
          stopStuff = star.split(/\|[0-9]*;/).each do |route|
            info = route.split("||")
            stopInfo = info[0].split("|")
            times = info[1]
            lat = stopInfo[0].to_f
            lon = stopInfo[1].to_f
            stop = stopInfo[2]
            direction = stopInfo[3]
            ri = RouteInfo.new(stop, direction, lat, lon, rte_number.to_i)
            if /<br>/.match(times)
              times.split("<br>").each do |t|
                ri.addTime(t)
              end
            end
            
            routeInfo << ri
          end
        end
      end
    end
    
    data = routeInfo
    if data != nil
      @response.data = data
      @response.status = "GOOD"
    else
      @response.data = {Error: {message: "Stop not found"}}
      @response.status = "FAIL"
    end
    
    
    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @response }
    end
  end
  
  def major_stops
    @response = Response.new
    @response.status = "GOOD"
    rte_number = params[:route]
    major_minor = params[:which]
    ind = 0
    
    if major_minor.upcase == "MINOR"
      ind = 2
    else
      ind = 0
    end
    
    urlResponse = ""
    routeInfo = []
  
    uri = URI.parse("http://webwatch.itsmarta.com/UpdateWebMap.aspx?u=" + rte_number)
    urlResponse = Net::HTTP.get_response(uri)
    
    indexOfFirstStar = urlResponse.body.index("*")
    if indexOfFirstStar != nil
      
      @response.currentTime = urlResponse.body[0...indexOfFirstStar]
      
      unparsedRouteInfo = urlResponse.body[indexOfFirstStar+1..-1]
      star = unparsedRouteInfo.split("*")
      
      star[ind].split(/\|[0-9]*;/).each do |route|
        info = route.split("||")
        stopInfo = info[0].split("|")
        times = info[1]
        lat = stopInfo[0].to_f
        lon = stopInfo[1].to_f
        stop = stopInfo[2]
        direction = stopInfo[3]
        ri = RouteInfo.new(stop, direction, lat, lon, rte_number.to_i)
        if /<br>/.match(times)
          times.split("<br>").each do |t|
            ri.addTime(t)
          end
        end
        
        routeInfo << ri
      end
    end
    
    data = routeInfo
    if data != nil
      @response.data = routeInfo
      @response.status = "GOOD"
    else
      @response.data = {Error: {message: "Stop not found"}}
      @response.status = "FAIL"
    end
    
    
    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @response }
    end
  end
  
end

class Response
  attr_accessor :status, :currentTime, :data
end

class RouteInfo
  attr_accessor :lat, :lon, :stop, :direction, :route
  
  def initialize(stop, direction, lat, lon, route)
    @stop = stop
    @direction = direction
    @lat = lat
    @lon = lon
    @route = route
    @times = []
  end
  
  def addTime(time)
    @times.push(time)
  end
  
  def times
    @times
  end
  
  def self.find_by_stop(stop)
    found = nil
    ObjectSpace.each_object(RouteInfo) { |o|
      found = o if o.stop.upcase == stop.upcase
    }
    found
  end
  
  def self.find_by_direction(direction)
    found = nil
    ObjectSpace.each_object(RouteInfo) { |o|
      found = o if o.direction.upcase == direction.upcase
    }
    found
  end
  
  def self.find_by_stop_and_direction(stop, direction)
    found = nil
    ObjectSpace.each_object(RouteInfo) { |o|
      found = o if o.stop.gsub('.','').gsub(' ','').upcase == stop.upcase && o.direction.upcase == direction.upcase
    }
    found
  end
  
end

class Bus
  attr_accessor :num, :next_stop, :lat, :lon, :route, :direction
end