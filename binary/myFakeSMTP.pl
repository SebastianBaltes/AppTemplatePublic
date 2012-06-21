#!/usr/bin/perl

################################################################################
#                                                                              #
# myFakeSMTP.pl Server V0.1.005, 2008-07-13                                    #
#                                                                              #
# Author: Matthias Lochen                                                      #
#                                                                              #
#                                                                              #
# This program is free software; you can redistribute it and/or modify it under#
# the terms of the GNU General Public License as published by the              #
# Free Software Foundation; either version 3 of the License,                   #
# or (at your option) any later version.                                       #
#                                                                              #
# This program is distributed in the hope that it will be useful, but          #
# WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY   #
# or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License      #
# for more details. You should have received a copy of the                     #
# GNU General Public License along with this program;                          #
# if not, see <http://www.gnu.org/licenses/>.                                  #
#                                                                              #
################################################################################


use IO::Handle;
use IO::Socket;
use Time::HiRes ('usleep');

use warnings;
use strict;

my $bindAddress = shift || 'localhost';
my $bindPort    = shift || 2525;
my $domain      = shift || "linux.local";
my $logDir      = shift || "/tmp/FAKE_SMTP/new";
my $alarm       = shift || 3;
my $tarPitMult  = shift || 0;   #multiplicator for tarPit-Sleep
my $reportSleep = shift || 0;
my $readLineUSleep = shift || 0;

my $exit_QUIT         = 0;  # if client submits "QUIT"
my $exit_ERROR        = 1;  # General Error 
my $exit_TIMEOUT      = 2;  # TimeOut
my $exit_MAIN_EXIT    = 3;  # did not leave by "QUIT" Command 
my $exit_QUIT_NO_DATA = 99; # if client submits "QUIT" without giving MailData

#print "Using domain=$domain, logDir=$logDir, alarm=$alarm\n";

## Codes taken from RFC-2821
my %codes = (

  "211" => "211 System status, or system help reply\r\n",
  "214" => "214 Help message\r\n",
  "220" => "220 $domain Service ready\r\n",
  "221" => "221 $domain Service closing transmission channel\r\n",
  "250" => "250 Requested mail action okay, completed\r\n",
  "251" => "251 User not local; will forward to <forward-path>\r\n",
  "252" => "252 Cannot VRFY user, but will accept message and attempt delivery\r\n",
    
  "354" => "354 Start mail input; end with <CRLF>.<CRLF>\r\n",
    
  "421" => "421 $domain Service not available, closing transmission channel\r\n",
  "450" => "450 Requested mail action not taken: mailbox unavailable\r\n",
  "451" => "451 Requested action aborted: local error in processing\r\n",
  "452" => "452 Requested action not taken: insufficient system storage\r\n",
    
  "500" => "500 Syntax error, command unrecognized\r\n",
  "501" => "501 Syntax error in parameters or arguments\r\n",
  "502" => "502 Command not implemented\r\n",
  "503" => "503 Bad sequence of commands\r\n",
  "504" => "504 Command parameter not implemented\r\n",
  "550" => "550 Requested action not taken: mailbox unavailable\r\n",
  "551" => "551 User not local; please try <forward-path>\r\n",
  "552" => "552 Requested mail action aborted: exceeded storage allocation\r\n",
  "553" => "553 Requested action not taken: mailbox name not allowed\r\n",
  "554" => "554 Transaction failed\r\n"
    
);

# CMD -> ResponseCode, TarPitSleep on Command, Method to call
#
my %cmds = (
    'EHLO'       => [ 500, 0                  ],
    'HELO'       => [ 250, 0                  ],
    'MAIL FROM:' => [ 250, 0                  ],
    'RCPT TO:'   => [ 250, 0                  ],
    'DATA'       => [ 354, 0, \&setDataMode   ],
    'RSET'       => [ 250, 0, \&reset         ],
    'HELP'       => [ 502, 0                  ],
    'NOOP'       => [ 250, 0                  ],
    'QUIT'       => [ 221, 0, \&finishServer ],    
    #"VRFY",
    #"EXPN",
);
my $tarPitSleep_DATA_FINISHED = 0;
my $tarPitSleep_UNKNOWN_CMD   = 0;

################################################################################
#                                                                              #
#                                 M A I N                                      #
#                                                                              #
################################################################################

# Note: socket stuff has hastly been added for selenium testing
# Tarpit stuff has beed removed therefore. 

STDOUT->autoflush(1);

my $socket = IO::Socket::INET->new(
	LocalAddress => $bindAddress, 
	LocalPort    => $bindPort,
	Proto        => 'tcp',
	Listen => SOMAXCONN, 
	Reuse => 1
) or die "Could not setup local socket: $!\n";

my $cmdRegEx = join('|',keys %cmds);
my $fileCounter = 0;
my $isDataMode;
my $wasDataMode;	
my $connection;

while (defined($connection = $socket->accept())) {		
		
	$isDataMode=0;
	$wasDataMode=0;#	
	
	open(FLOG, ">> $logDir/".createLogFileName()) or myDie(450);	
	FLOG->autoflush(1);		
	
	# 1. announce Hello-Message
	myPrint($codes{220});
	
	while(<$connection>) {
	    usleep($readLineUSleep) if $readLineUSleep;	
	 
	    unless ($isDataMode) {
	        my $c;
	        unless (($c) = /^($cmdRegEx)/io) {
	            myPrint($codes{500});            
	            next;
	        }
	        $c = uc $c;        
	        my ($printCode, $tarPitSleep, $doSub) = @{$cmds{$c}};            
	        myPrint($codes{$printCode});
	        &$doSub if $doSub;
	    }
	    else {	    	
	        #check if last "point" while in "DATA"-Mode
	        if (/^\.\r?\n$/) {
	            $isDataMode=0;
	            myPrint($codes{250});
	        }
	        else {
	        	print FLOG;
	        }
	    }
	    last unless defined($connection);
	}
}

sub setDataMode {
    $isDataMode = 1;
    $wasDataMode = 1;
}
	
sub reset {
    $isDataMode = 0;
}	

sub finishServer {
	$connection->close();
	$connection = undef; 
    close(FLOG);
}	
	
sub myPrint {
   	print $connection @_ if defined $connection;
}

sub myDie {
     print $codes{$_[0]};
     exit $exit_ERROR;
}

sub createLogFileName {
	my $pid = $$;
	$pid =~ s/[^0-9]//g;
	
	my $nowTime = time;
	my $fName = sprintf("%05d_", $fileCounter++).$nowTime.
	            '_'.
	            sprintf("%02d:%02d:%02d", (localtime($nowTime))[2,1,0]).
	            '_'.
	            "pid".
	            $pid.
	            '_'.
	            int(rand(100000));
} 








