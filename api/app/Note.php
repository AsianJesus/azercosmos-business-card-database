<?php


namespace App;


use Illuminate\Database\Eloquent\Model;
use Illuminate\Support\Str;

class Note extends Model
{
    protected $table = 'bcard_notes';

    protected $fillable = [
        'business_card_id', 'note'
    ];

    public function business_card() {
        return $this->belongsTo(BusinessCard::class);
    }

    protected static function boot()
    {
        parent::boot();

        self::creating(function ($note) {
            $note->id = Str::uuid();
        });
    }

    public function getKeyName()
    {
        return 'id';
    }

    public function getKeyType()
    {
        return 'string';
    }

    public function getIncrementing()
    {
        return false;
    }

}