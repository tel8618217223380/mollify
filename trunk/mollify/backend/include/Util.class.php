<?php
	class Util {
		public static function inBytes($amount) {
		    $amount = trim($amount);
		    $last = strtolower($amount[strlen($amount)-1]);
		    
		    switch ($last) {
		        case 'g':
		            $amount *= 1024;
		        case 'm':
		            $amount *= 1024;
		        case 'k':
		            $amount *= 1024;
		    }
		
		    return (int)$amount;
		}
		
		function base64_url_encode($input) {
			return strtr(base64_encode($input), '+/=', '-_,');
		}

		function base64_url_decode($input) {
			return base64_decode(strtr($input, '-_,', '+/='));
		}
		
		static function toString($a) {
			if (is_array($a)) return self::array2str($a);
			return $a;
		}
		
		static function array2str($a, $ignoredKeys = NULL) {
			if ($a === NULL) return "NULL";
			
			$r = "{";
			$first = TRUE;
			foreach($a as $k=>$v) {
				if ($ignoredKeys != null and in_array($k, $ignoredKeys)) continue;
				
				if (!$first) $r .= ", ";
				
				$val = $v;
				if (is_array($v)) $val = self::array2str($v);
				
				$r .= $k.':'.$val;
				$first = FALSE;
			}
			return $r."}";
		}
	}
?>