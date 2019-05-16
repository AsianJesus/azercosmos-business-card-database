<?php


namespace App\Jobs;


class LoadImage extends Job
{
    protected $url;
    public function __construct($url)
    {
        $this->url = $url;
    }

    public function handle() {
        $url = env('OTHER_SERVER_URL')."/$this->url";
        file_put_contents($this->url, file_get_contents($url));
    }
}